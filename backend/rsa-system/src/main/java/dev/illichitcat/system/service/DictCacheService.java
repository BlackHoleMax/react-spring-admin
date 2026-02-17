package dev.illichitcat.system.service;

import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.model.entity.DictItem;

import java.util.List;

/**
 * 字典缓存服务接口
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
public interface DictCacheService {

    /**
     * 获取字典缓存（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存
     * 3. 都未命中查数据库并回填缓存
     *
     * @param id 字典ID
     * @return 字典对象
     */
    Dict getDictFromCache(Long id);

    /**
     * 获取字典缓存（通过编码）
     *
     * @param dictCode 字典编码
     * @return 字典对象
     */
    Dict getDictFromCache(String dictCode);

    /**
     * 获取字典项缓存
     *
     * @param id 字典项ID
     * @return 字典项对象
     */
    DictItem getDictItemFromCache(Long id);

    /**
     * 获取字典项列表缓存
     *
     * @param dictId 字典ID
     * @return 字典项列表
     */
    List<DictItem> getDictItemsFromCache(Long dictId);

    /**
     * 获取字典项列表缓存（通过字典编码）
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<DictItem> getDictItemsFromCache(String dictCode);

    /**
     * 缓存字典
     *
     * @param dict 字典对象
     */
    void cacheDict(Dict dict);

    /**
     * 缓存字典项
     *
     * @param item 字典项对象
     */
    void cacheDictItem(DictItem item);

    /**
     * 缓存字典项列表
     *
     * @param dictId 字典ID
     * @param items  字典项列表
     */
    void cacheDictItems(Long dictId, List<DictItem> items);

    /**
     * 缓存字典项列表（通过字典编码）
     *
     * @param dictCode 字典编码
     * @param items    字典项列表
     */
    void cacheDictItems(String dictCode, List<DictItem> items);

    /**
     * 清除字典缓存
     *
     * @param id 字典ID
     */
    void evictDictCache(Long id);

    /**
     * 清除字典缓存（通过编码）
     *
     * @param dictCode 字典编码
     */
    void evictDictCache(String dictCode);

    /**
     * 清除字典项缓存
     *
     * @param id 字典项ID
     */
    void evictDictItemCache(Long id);

    /**
     * 清除字典项列表缓存
     *
     * @param dictId 字典ID
     */
    void evictDictItemsCache(Long dictId);

    /**
     * 清除字典项列表缓存（通过字典编码）
     *
     * @param dictCode 字典编码
     */
    void evictDictItemsCache(String dictCode);

    /**
     * 清除所有字典缓存
     */
    void evictAllDictCache();

    /**
     * 预热缓存
     */
    void warmUpCache();
}