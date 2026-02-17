package dev.illichitcat.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.illichitcat.common.common.constant.SystemConstants;
import dev.illichitcat.system.config.DictCacheProperties;
import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.model.entity.DictItem;
import dev.illichitcat.system.service.BaseCacheService;
import dev.illichitcat.system.service.DictCacheService;
import dev.illichitcat.system.service.DictItemService;
import dev.illichitcat.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典缓存服务实现
 * 采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存 + 异步刷新
 * 继承 BaseCacheService 消除重复代码
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Service
public class DictCacheServiceImpl extends BaseCacheService implements DictCacheService {

    @Autowired
    @Lazy
    private DictService dictService;

    @Autowired
    @Lazy
    private DictItemService dictItemService;

    @Autowired
    private DictCacheProperties cacheProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected boolean isCacheEnabled() {
        return !cacheProperties.isEnabled();
    }

    @Override
    protected boolean isCaffeineEnabled() {
        return !cacheProperties.getCaffeine().isEnabled();
    }

    @Override
    protected boolean isRedisEnabled() {
        return cacheProperties.getRedis().isEnabled();
    }

    @Override
    protected long getRedisExpireTime() {
        return cacheProperties.getRedis().getExpireTime();
    }

    @Override
    protected String getRedisPrefixByCacheName(String cacheName) {
        return switch (cacheName) {
            case "dict" -> cacheProperties.getDictPrefix();
            case "dictItem" -> cacheProperties.getDictItemPrefix();
            case "dictItems" -> cacheProperties.getDictItemsPrefix();
            default -> "";
        };
    }

    @Override
    public Dict getDictFromCache(Long id) {
        return getFromCache(id,
                "dict",
                dictPrefix -> cacheProperties.getDictPrefix() + id,
                dictId -> dictService.getById(dictId),
                dict -> {
                    cacheDict(dict);
                    return null;
                });
    }

    @Override
    public Dict getDictFromCache(String dictCode) {
        return getFromCache(dictCode,
                "dict",
                dictPrefix -> cacheProperties.getDictPrefix() + "code:" + dictCode,
                code -> dictService.selectDictByCode(code),
                dict -> {
                    cacheDict(dict, true);
                    return null;
                });
    }

    @Override
    public DictItem getDictItemFromCache(Long id) {
        return getFromCache(id,
                "dictItem",
                prefix -> cacheProperties.getDictItemPrefix() + id,
                itemId -> dictItemService.getById(itemId),
                item -> {
                    cacheDictItem(item);
                    return null;
                });
    }

    @Override
    public List<DictItem> getDictItemsFromCache(Long dictId) {
        if (isCacheEnabled()) {
            return dictItemService.selectItemsByDictId(dictId);
        }

        // 1. 先查 Caffeine 一级缓存
        List<DictItem> value = getFromCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + dictId);
        if (value != null) {
            log.debug("Caffeine 命中: cacheName=dictItems, key={}", dictId);
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        String rawValue = getFromRedisRaw(cacheProperties.getDictItemsPrefix() + dictId);
        if (rawValue != null) {
            log.debug("Redis 命中: cacheName=dictItems, key={}", dictId);
            try {
                // 反序列化为 List<DictItem>
                List<DictItem> items = objectMapper.readValue(rawValue,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DictItem.class));
                // 回填 Caffeine 缓存
                putToCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + dictId, items);
                return items;
            } catch (Exception e) {
                log.error("反序列化 Redis 缓存失败, key={}", dictId, e);
            }
        }

        // 3. 都未命中查数据库
        log.debug("缓存未命中，查询数据库: cacheName=dictItems, key={}", dictId);
        List<DictItem> dbValue = dictItemService.selectItemsByDictId(dictId);
        if (dbValue != null) {
            // 回填缓存
            cacheDictItems(dictId, dbValue, true);
        }

        return dbValue;
    }

    @Override
    public List<DictItem> getDictItemsFromCache(String dictCode) {
        if (isCacheEnabled()) {
            return dictItemService.selectItemsByDictCode(dictCode);
        }

        // 1. 先查 Caffeine 一级缓存
        List<DictItem> value = getFromCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + "code:" + dictCode);
        if (value != null) {
            log.debug("Caffeine 命中: cacheName=dictItems, key={}", dictCode);
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        String rawValue = getFromRedisRaw(cacheProperties.getDictItemsPrefix() + "code:" + dictCode);
        if (rawValue != null) {
            log.debug("Redis 命中: cacheName=dictItems, key={}", dictCode);
            try {
                // 反序列化为 List<DictItem>
                List<DictItem> items = objectMapper.readValue(rawValue,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DictItem.class));
                // 回填 Caffeine 缓存
                putToCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + "code:" + dictCode, items);
                return items;
            } catch (Exception e) {
                log.error("反序列化 Redis 缓存失败, key={}", dictCode, e);
            }
        }

        // 3. 都未命中查数据库
        log.debug("缓存未命中，查询数据库: cacheName=dictItems, key={}", dictCode);
        List<DictItem> dbValue = dictItemService.selectItemsByDictCode(dictCode);
        if (dbValue != null) {
            // 回填缓存
            cacheDictItems(dictCode, dbValue, true);
        }

        return dbValue;
    }

    @Override
    public void cacheDict(Dict dict) {
        cacheDict(dict, false);
    }

    /**
     * 缓存字典
     */
    private void cacheDict(Dict dict, boolean skipRedis) {
        if (dict == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine("dict", cacheProperties.getDictPrefix() + dict.getId(), dict);
        putToCaffeine("dict", cacheProperties.getDictPrefix() + "code:" + dict.getDictCode(), dict);

        // 写入 Redis 二级缓存
        if (isRedisEnabled() && !skipRedis) {
            asyncPutToRedis(cacheProperties.getDictPrefix() + dict.getId(), dict);
            asyncPutToRedis(cacheProperties.getDictPrefix() + "code:" + dict.getDictCode(), dict);
        }

        log.debug("缓存字典成功, dictId={}, dictCode={}", dict.getId(), dict.getDictCode());
    }

    @Override
    public void cacheDictItem(DictItem item) {
        if (item == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine("dictItem", cacheProperties.getDictItemPrefix() + item.getId(), item);

        // 写入 Redis 二级缓存
        if (isRedisEnabled()) {
            asyncPutToRedis(cacheProperties.getDictItemPrefix() + item.getId(), item);
        }

        log.debug("缓存字典项成功, itemId={}", item.getId());
    }

    @Override
    public void cacheDictItems(Long dictId, List<DictItem> items) {
        cacheDictItems(dictId, items, false);
    }

    /**
     * 缓存字典项列表
     */
    private void cacheDictItems(Long dictId, List<DictItem> items, boolean skipRedis) {
        if (dictId == null || items == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + dictId, items);

        // 写入 Redis 二级缓存
        if (isRedisEnabled() && !skipRedis) {
            asyncPutToRedis(cacheProperties.getDictItemsPrefix() + dictId, items);
        }

        log.debug("缓存字典项列表成功, dictId={}, itemCount={}", dictId, items.size());
    }

    @Override
    public void cacheDictItems(String dictCode, List<DictItem> items) {
        cacheDictItems(dictCode, items, false);
    }

    /**
     * 缓存字典项列表（通过字典编码）
     */
    private void cacheDictItems(String dictCode, List<DictItem> items, boolean skipRedis) {
        if (dictCode == null || items == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine("dictItems", cacheProperties.getDictItemsPrefix() + "code:" + dictCode, items);

        // 写入 Redis 二级缓存
        if (isRedisEnabled() && !skipRedis) {
            asyncPutToRedis(cacheProperties.getDictItemsPrefix() + "code:" + dictCode, items);
        }

        log.debug("缓存字典项列表成功, dictCode={}, itemCount={}", dictCode, items.size());
    }

    @Override
    public void evictDictCache(Long id) {
        evictCache("dict", dictPrefix -> cacheProperties.getDictPrefix() + id);
        evictDictItemsCache(id);
        log.debug("清除字典缓存成功, dictId={}", id);
    }

    @Override
    public void evictDictCache(String dictCode) {
        evictCache("dict", dictPrefix -> cacheProperties.getDictPrefix() + "code:" + dictCode);
        evictDictItemsCache(dictCode);
        log.debug("清除字典缓存成功, dictCode={}", dictCode);
    }

    @Override
    public void evictDictItemCache(Long id) {
        evictCache("dictItem", prefix -> cacheProperties.getDictItemPrefix() + id);
        log.debug("清除字典项缓存成功, itemId={}", id);
    }

    @Override
    public void evictDictItemsCache(Long dictId) {
        evictCache("dictItems", prefix -> cacheProperties.getDictItemsPrefix() + dictId);
        log.debug("清除字典项列表缓存成功, dictId={}", dictId);
    }

    @Override
    public void evictDictItemsCache(String dictCode) {
        evictCache("dictItems", prefix -> cacheProperties.getDictItemsPrefix() + "code:" + dictCode);
        log.debug("清除字典项列表缓存成功, dictCode={}", dictCode);
    }

    @Override
    public void evictAllDictCache() {
        evictAllCache("dict", "dictItem", "dictItems");
    }

    @Override
    public void warmUpCache() {
        if (isCacheEnabled()) {
            return;
        }
        log.info("开始预热字典缓存, source={}", SystemConstants.CacheSource.MANUAL);

        List<Dict> dictList = dictService.list();
        for (Dict dict : dictList) {
            try {
                cacheDict(dict);
                List<DictItem> items = dictItemService.selectItemsByDictId(dict.getId());
                cacheDictItems(dict.getId(), items);
                cacheDictItems(dict.getDictCode(), items);
            } catch (Exception e) {
                log.error("预热字典缓存失败, dictId={}, dictCode={}", dict.getId(), dict.getDictCode(), e);
            }
        }

        log.info("字典缓存预热完成, 总字典数={}, source={}", dictList.size(), SystemConstants.CacheSource.MANUAL);
    }

}