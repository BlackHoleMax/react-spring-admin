package dev.illichitcat.system.service.impl;

import dev.illichitcat.common.common.constant.RedisConstants;
import dev.illichitcat.system.model.vo.CacheInfoVO;
import dev.illichitcat.system.model.vo.CommandStatVO;
import dev.illichitcat.system.model.vo.KeyDetailVO;
import dev.illichitcat.system.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 缓存监控服务实现类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CacheInfoVO getCacheInfo() {
        Properties info = redisTemplate.execute((RedisCallback<Properties>) connection -> connection.serverCommands().info());
        Properties commandStats = redisTemplate.execute((RedisCallback<Properties>) connection -> connection.serverCommands().info("commandstats"));
        Long dbSize = redisTemplate.execute((RedisCallback<Long>) connection -> connection.serverCommands().dbSize());

        CacheInfoVO cacheInfo = new CacheInfoVO();

        // Redis信息
        Map<String, String> redisInfo = new HashMap<>(32);
        if (info != null) {
            redisInfo.put("version", info.getProperty("redis_version"));
            redisInfo.put("mode", info.getProperty("redis_mode"));
            redisInfo.put("os", info.getProperty("os"));
            redisInfo.put("arch_bits", info.getProperty("arch_bits"));
            redisInfo.put("tcp_port", info.getProperty("tcp_port"));
            redisInfo.put("connected_clients", info.getProperty("connected_clients"));
            redisInfo.put("used_memory", info.getProperty("used_memory"));
            redisInfo.put("used_memory_human", info.getProperty("used_memory_human"));
            redisInfo.put("used_memory_rss", info.getProperty("used_memory_rss"));
            redisInfo.put("used_memory_rss_human", info.getProperty("used_memory_rss_human"));
            redisInfo.put("used_memory_peak", info.getProperty("used_memory_peak"));
            redisInfo.put("used_memory_peak_human", info.getProperty("used_memory_peak_human"));
            redisInfo.put("used_memory_lua", info.getProperty("used_memory_lua"));
            redisInfo.put("used_memory_lua_human", info.getProperty("used_memory_lua_human"));
            redisInfo.put("maxmemory", info.getProperty("maxmemory"));
            redisInfo.put("maxmemory_human", info.getProperty("maxmemory_human"));
            redisInfo.put("mem_fragmentation_ratio", info.getProperty("mem_fragmentation_ratio"));
            redisInfo.put("mem_allocator", info.getProperty("mem_allocator"));
            redisInfo.put("uptime_in_days", info.getProperty("uptime_in_days"));
            redisInfo.put("uptime_in_seconds", info.getProperty("uptime_in_seconds"));
            redisInfo.put("used_cpu_sys", info.getProperty("used_cpu_sys"));
            redisInfo.put("used_cpu_user", info.getProperty("used_cpu_user"));
            redisInfo.put("used_cpu_sys_children", info.getProperty("used_cpu_sys_children"));
            redisInfo.put("used_cpu_user_children", info.getProperty("used_cpu_user_children"));
            redisInfo.put("instantaneous_input_kbps", info.getProperty("instantaneous_input_kbps"));
            redisInfo.put("instantaneous_output_kbps", info.getProperty("instantaneous_output_kbps"));
            redisInfo.put("gcc_version", info.getProperty("gcc_version", "N/A"));
            redisInfo.put("bind", info.getProperty("bind", "N/A"));
            redisInfo.put("aof_enabled", info.getProperty("aof_enabled"));
            redisInfo.put("aof_rewrite_in_progress", info.getProperty("aof_rewrite_in_progress"));
            redisInfo.put("rdb_last_bgsave_time_sec", info.getProperty("rdb_last_bgsave_time_sec"));
            redisInfo.put("rdb_last_bgsave_status", info.getProperty("rdb_last_bgsave_status"));
        }
        cacheInfo.setInfo(redisInfo);

        // 数据库大小
        cacheInfo.setDbSize(dbSize != null ? dbSize.intValue() : 0);

        // 命令统计
        List<CommandStatVO> commandStatsList = new ArrayList<>(commandStats != null ? commandStats.size() : 0);
        if (commandStats != null) {
            commandStats.stringPropertyNames().forEach(key -> {
                if (key.startsWith(RedisConstants.CMDSTAT_PREFIX)) {
                    String property = commandStats.getProperty(key);
                    String command = key.substring(RedisConstants.CMDSTAT_PREFIX.length());
                    String calls = property.substring(property.indexOf("calls=") + 6, property.indexOf(","));
                    String usec = property.substring(property.indexOf("usec=") + 5, property.indexOf(",usec_per_call="));

                    CommandStatVO stat = new CommandStatVO();
                    stat.setName(command);
                    stat.setCalls(Long.parseLong(calls));
                    stat.setUsec(Long.parseLong(usec));
                    commandStatsList.add(stat);
                }
            });
        }
        cacheInfo.setCommandStats(commandStatsList);

        return cacheInfo;
    }

    @Override
    public List<String> getKeys(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        List<String> keyList = new ArrayList<>(keys.size());
        keyList.addAll(keys);
        Collections.sort(keyList);
        return keyList;
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public KeyDetailVO getKeyDetail(String key) {
        KeyDetailVO detail = new KeyDetailVO();

        String keyType = getKeyType(key);
        String valueStr = getKeyValueByType(key, keyType);

        detail.setValue(valueStr);
        detail.setType(keyType.toUpperCase());
        detail.setTtl(redisTemplate.getExpire(key));

        return detail;
    }

    /**
     * 获取键的类型
     */
    private String getKeyType(String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] keyBytes = redisTemplate.getStringSerializer().serialize(key);
            DataType dataType = keyBytes != null ? connection.keyCommands().type(keyBytes) : null;
            return dataType != null ? dataType.code() : RedisConstants.TYPE_NONE;
        });
    }

    /**
     * 根据键类型获取键值
     */
    private String getKeyValueByType(String key, String keyType) {
        return switch (keyType) {
            case RedisConstants.TYPE_STRING -> getStringValue(key);
            case RedisConstants.TYPE_HASH -> getHashValue(key);
            case RedisConstants.TYPE_LIST -> getListValue(key);
            case RedisConstants.TYPE_SET -> getSetValue(key);
            case RedisConstants.TYPE_ZSET -> getZsetValue(key);
            default -> "不支持的类型: " + keyType;
        };
    }

    /**
     * 获取字符串类型的值
     */
    private String getStringValue(String key) {
        byte[] valueBytes = redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            byte[] keyBytes = redisTemplate.getStringSerializer().serialize(key);
            return keyBytes != null ? connection.stringCommands().get(keyBytes) : null;
        });
        return valueBytes != null ? new String(valueBytes, java.nio.charset.StandardCharsets.UTF_8) : "";
    }

    /**
     * 获取Hash类型的值
     */
    private String getHashValue(String key) {
        Map<byte[], byte[]> hashBytes = redisTemplate.execute((RedisCallback<Map<byte[], byte[]>>) connection -> {
            byte[] keyBytes = redisTemplate.getStringSerializer().serialize(key);
            return keyBytes != null ? connection.hashCommands().hGetAll(keyBytes) : null;
        });
        if (hashBytes == null || hashBytes.isEmpty()) {
            return "";
        }
        Map<String, String> hashMap = new HashMap<>(hashBytes.size());
        for (Map.Entry<byte[], byte[]> entry : hashBytes.entrySet()) {
            hashMap.put(new String(entry.getKey(), java.nio.charset.StandardCharsets.UTF_8),
                    new String(entry.getValue(), java.nio.charset.StandardCharsets.UTF_8));
        }
        return hashMap.toString();
    }

    /**
     * 获取List类型的值
     */
    private String getListValue(String key) {
        List<byte[]> listBytes = redisTemplate.execute((RedisCallback<List<byte[]>>) connection -> {
            byte[] keyBytes = redisTemplate.getStringSerializer().serialize(key);
            return keyBytes != null ? connection.listCommands().lRange(keyBytes, 0, -1) : null;
        });
        return convertBytesToStrings(listBytes).toString();
    }

    /**
     * 获取Set类型的值
     */
    private String getSetValue(String key) {
        Set<byte[]> setBytes = redisTemplate.execute((RedisCallback<Set<byte[]>>) connection -> {
            byte[] keyBytes = redisTemplate.getStringSerializer().serialize(key);
            return keyBytes != null ? connection.setCommands().sMembers(keyBytes) : null;
        });
        return convertBytesToStrings(setBytes).toString();
    }

    /**
     * 获取ZSet类型的值
     */
    private String getZsetValue(String key) {
        Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> zsetTuples =
                redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        if (zsetTuples == null || zsetTuples.isEmpty()) {
            return "";
        }
        List<String> stringValues = new ArrayList<>(zsetTuples.size());
        for (org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object> tuple : zsetTuples) {
            Object value = tuple.getValue();
            Double score = tuple.getScore();
            if (value != null) {
                stringValues.add(value + ":" + score);
            }
        }
        return stringValues.toString();
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void setTtl(String key, Long ttl) {
        redisTemplate.expire(key, ttl, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void clearDb() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 将字节数组集合转换为字符串列表
     */
    private List<String> convertBytesToStrings(Collection<byte[]> bytesCollection) {
        if (bytesCollection == null || bytesCollection.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> stringValues = new ArrayList<>(bytesCollection.size());
        for (byte[] bytes : bytesCollection) {
            stringValues.add(new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
        }
        return stringValues;
    }
}