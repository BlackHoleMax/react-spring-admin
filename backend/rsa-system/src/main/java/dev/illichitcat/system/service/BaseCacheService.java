package dev.illichitcat.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存服务基类
 * 封装通用的缓存操作逻辑，采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存
 * 消除缓存服务实现类中的重复代码
 *
 * @author Illichitcat
 * @since 2026/01/14
 */
@Slf4j
public abstract class BaseCacheService {

    @Autowired
    protected CacheManager cacheManager;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * 获取缓存是否启用
     *
     * @return true-启用，false-禁用
     */
    protected abstract boolean isCacheEnabled();

    /**
     * 获取 Caffeine 缓存是否启用
     *
     * @return true-启用，false-禁用
     */
    protected abstract boolean isCaffeineEnabled();

    /**
     * 获取 Redis 缓存是否启用
     *
     * @return true-启用，false-禁用
     */
    protected abstract boolean isRedisEnabled();

    /**
     * 获取 Redis 缓存过期时间（分钟）
     *
     * @return 过期时间（分钟）
     */
    protected abstract long getRedisExpireTime();

    /**
     * 根据缓存名称获取 Redis Key 前缀
     *
     * @param cacheName 缓存名称
     * @return Redis Key 前缀
     */
    protected abstract String getRedisPrefixByCacheName(String cacheName);

    /**
     * 通用缓存获取方法（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存（返回原始字符串，由子类处理反序列化）
     * 3. 都未命中查数据库并回填缓存
     *
     * @param key          缓存键
     * @param cacheName    缓存名称
     * @param keyGenerator Key 生成器
     * @param dbLoader     数据库加载器
     * @param cacheWriter  缓存写入器
     * @param <K>          键类型
     * @param <V>          值类型
     * @return 缓存值
     */
    protected <K, V> V getFromCache(K key,
                                    String cacheName,
                                    Function<String, String> keyGenerator,
                                    Function<K, V> dbLoader,
                                    Function<V, Void> cacheWriter) {
        if (isCacheEnabled()) {
            return dbLoader.apply(key);
        }

        // 1. 先查 Caffeine 一级缓存
        V value = getFromCaffeine(cacheName, keyGenerator.apply(""));
        if (value != null) {
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        V redisValue = getFromRedis(keyGenerator.apply(getRedisPrefixByCacheName(cacheName)));
        if (redisValue != null) {
            // 回填 Caffeine 缓存
            putToCaffeine(cacheName, keyGenerator.apply(""), redisValue);
            return redisValue;
        }

        // 3. 都未命中查数据库
        log.debug("缓存未命中，查询数据库: cacheName={}, key={}", cacheName, key);
        V dbValue = dbLoader.apply(key);
        if (dbValue != null) {
            // 回填缓存
            cacheWriter.apply(dbValue);
        }

        return dbValue;
    }

    /**
     * 通用缓存获取方法（Cache-Aside 模式，直接获取对象）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存（直接获取对象）
     * 3. 都未命中查数据库并回填缓存
     *
     * @param key          缓存键
     * @param cacheName    缓存名称
     * @param keyGenerator Key 生成器
     * @param dbLoader     数据库加载器
     * @param cacheWriter  缓存写入器
     * @param <K>          键类型
     * @param <V>          值类型
     * @return 缓存值
     */
    protected <K, V> V getFromCacheWithRedis(K key,
                                             String cacheName,
                                             Function<String, String> keyGenerator,
                                             Function<K, V> dbLoader,
                                             Function<V, Void> cacheWriter) {
        if (isCacheEnabled()) {
            return dbLoader.apply(key);
        }

        // 1. 先查 Caffeine 一级缓存
        V value = getFromCaffeine(cacheName, keyGenerator.apply(""));
        if (value != null) {
            log.debug("Caffeine 命中: cacheName={}, key={}", cacheName, key);
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        value = getFromRedis(keyGenerator.apply(getRedisPrefixByCacheName(cacheName)));
        if (value != null) {
            // 回填 Caffeine 缓存
            putToCaffeine(cacheName, keyGenerator.apply(""), value);
            return value;
        }

        // 3. 都未命中查数据库
        log.debug("缓存未命中，从数据库加载: cacheName={}, key={}", cacheName, key);
        value = dbLoader.apply(key);

        // 4. 回填缓存
        if (value != null) {
            cacheWriter.apply(value);
        }

        return value;
    }

    /**
     * 缓存数据到 Caffeine 和 Redis
     *
     * @param cacheName 缓存名称
     * @param key       Key 生成器
     * @param value     缓存值
     * @param <T>       值类型
     */
    protected <T> void cacheData(String cacheName, Function<String, String> key, T value) {
        if (isCacheEnabled() || value == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine(cacheName, key.apply(""), value);

        // 写入 Redis 二级缓存
        if (isRedisEnabled()) {
            asyncPutToRedis(key.apply(getRedisPrefixByCacheName(cacheName)), value);
        }
    }

    /**
     * 清除缓存
     *
     * @param cacheName 缓存名称
     * @param key       Key 生成器
     */
    protected void evictCache(String cacheName, Function<String, String> key) {
        if (isCacheEnabled()) {
            return;
        }
        evictFromCaffeine(cacheName, key.apply(""));
        evictFromRedis(key.apply(getRedisPrefixByCacheName(cacheName)));
    }

    /**
     * 清除所有缓存
     *
     * @param cacheNames 缓存名称列表
     */
    protected void evictAllCache(String... cacheNames) {
        if (isCacheEnabled()) {
            return;
        }

        // 清除 Caffeine 缓存
        for (String cacheName : cacheNames) {
            clearCaffeine(cacheName);
        }

        // 清除 Redis 缓存
        if (isRedisEnabled()) {
            for (String cacheName : cacheNames) {
                String prefix = getRedisPrefixByCacheName(cacheName);
                if (prefix != null && !prefix.isEmpty()) {
                    redisTemplate.delete(redisTemplate.keys(prefix + "*"));
                }
            }
        }

        log.info("清除所有缓存成功, cacheNames={}", (Object) cacheNames);
    }

    // ==================== Caffeine 缓存操作 ====================

    /**
     * 从 Caffeine 获取缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param <T>       值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    protected <T> T getFromCaffeine(String cacheName, String key) {
        if (isCaffeineEnabled()) {
            return null;
        }
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            org.springframework.cache.Cache.ValueWrapper wrapper = cache.get(key);
            return wrapper != null ? (T) wrapper.get() : null;
        }
        return null;
    }

    /**
     * 写入 Caffeine 缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值
     * @param <T>       值类型
     */
    protected <T> void putToCaffeine(String cacheName, String key, T value) {
        if (isCaffeineEnabled()) {
            return;
        }
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * 清除 Caffeine 缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     */
    protected void evictFromCaffeine(String cacheName, String key) {
        if (isCaffeineEnabled()) {
            return;
        }
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * 清空 Caffeine 缓存
     *
     * @param cacheName 缓存名称
     */
    protected void clearCaffeine(String cacheName) {
        if (isCaffeineEnabled()) {
            return;
        }
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    // ==================== Redis 缓存操作 ====================

    /**
     * 从 Redis 获取原始缓存字符串
     *
     * @param key 缓存键
     * @return 原始缓存字符串
     */
    protected String getFromRedisRaw(String key) {
        if (!isRedisEnabled()) {
            return null;
        }
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return cached.toString();
            }
        } catch (Exception e) {
            log.error("获取 Redis 缓存失败, key={}", key, e);
        }
        return null;
    }

    /**
     * 从 Redis 获取缓存并反序列化
     * 注意：此方法仅用于返回非 List 类型的数据
     * 对于 List 类型，请使用 getFromRedisAsList 方法
     *
     * @param key 缓存键
     * @param <T> 值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    protected <T> T getFromRedis(String key) {
        if (!isRedisEnabled()) {
            return null;
        }
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                // 如果是 String 类型，说明是 JSON 字符串，需要反序列化
                if (cached instanceof String jsonValue) {
                    return (T) objectMapper.readValue(jsonValue, Object.class);
                }
                return (T) cached;
            }
        } catch (Exception e) {
            log.error("获取 Redis 缓存失败, key={}", key, e);
        }
        return null;
    }

    /**
     * 从 Redis 获取 List 类型的缓存并反序列化
     *
     * @param key 缓存键
     * @param <T> 列表元素类型
     * @return 缓存值列表
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getFromRedisAsList(String key) {
        if (!isRedisEnabled()) {
            return null;
        }
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                // 如果是 String 类型，说明是 JSON 字符串，需要反序列化
                if (cached instanceof String jsonValue) {
                    return objectMapper.readValue(jsonValue,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                }
                // 如果已经是 List 类型，直接返回
                if (cached instanceof List) {
                    return (List<T>) cached;
                }
            }
        } catch (Exception e) {
            log.error("获取 Redis 缓存失败, key={}", key, e);
        }
        return null;
    }

    /**
     * 异步写入 Redis 缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   值类型
     */
    @Async
    protected <T> void asyncPutToRedis(String key, T value) {
        if (!isRedisEnabled()) {
            CompletableFuture.completedFuture(null);
            return;
        }
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, getRedisExpireTime(), TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("异步写入 Redis 缓存失败, key={}", key, e);
        }
        CompletableFuture.completedFuture(null);
    }

    /**
     * 清除 Redis 缓存
     *
     * @param key 缓存键
     */
    protected void evictFromRedis(String key) {
        if (!isRedisEnabled()) {
            return;
        }
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除 Redis 缓存失败, key={}", key, e);
        }
    }
}