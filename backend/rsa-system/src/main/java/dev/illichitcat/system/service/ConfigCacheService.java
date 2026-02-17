package dev.illichitcat.system.service;

import dev.illichitcat.system.model.entity.Config;

/**
 * 系统配置缓存服务接口
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
public interface ConfigCacheService {

    /**
     * 获取配置缓存（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存
     * 3. 都未命中查数据库并回填缓存
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    Config getConfigFromCache(String configKey);

    /**
     * 获取配置值缓存
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValueFromCache(String configKey);

    /**
     * 缓存配置
     *
     * @param configKey 配置键
     * @param config    配置对象
     */
    void cacheConfig(String configKey, Config config);

    /**
     * 缓存配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void cacheConfigValue(String configKey, String configValue);

    /**
     * 清除配置缓存
     * 根据配置键清除指定的配置对象缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法不会清除配置值缓存，如需同时清除配置值缓存，请调用 evictConfigValueCache
     *
     * @param configKey 配置键，用于标识要清除的配置
     */
    void evictConfigCache(String configKey);

    /**
     * 清除配置值缓存
     * 根据配置键清除指定的配置值缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法不会清除配置对象缓存，如需同时清除配置对象缓存，请调用 evictConfigCache
     *
     * @param configKey 配置键，用于标识要清除的配置值
     */
    void evictConfigValueCache(String configKey);

    /**
     * 清除所有配置缓存
     * 清除系统中所有的配置和配置值缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法通常在系统配置批量更新或系统重置时调用
     */
    void evictAllConfigCache();

    /**
     * 预热缓存
     */
    void warmUpCache();
}