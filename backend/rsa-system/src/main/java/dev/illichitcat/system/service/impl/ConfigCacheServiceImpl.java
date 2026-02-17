package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.illichitcat.system.config.ConfigCacheProperties;
import dev.illichitcat.system.dao.mapper.ConfigMapper;
import dev.illichitcat.system.model.entity.Config;
import dev.illichitcat.system.service.BaseCacheService;
import dev.illichitcat.system.service.ConfigCacheService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * 系统配置缓存服务实现
 * 采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存 + 异步刷新
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Service
public class ConfigCacheServiceImpl extends BaseCacheService implements ConfigCacheService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ConfigCacheProperties cacheProperties;

    @Resource(name = "virtualThreadExecutor")
    private ExecutorService virtualThreadExecutor;

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
        return cacheProperties.getConfigPrefix();
    }

    @Override
    public Config getConfigFromCache(String configKey) {
        return getFromCacheWithRedis(configKey,
                "config",
                prefix -> prefix + configKey,
                key -> {
                    LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Config::getConfigKey, key)
                            .eq(Config::getStatus, 1)
                            .eq(Config::getDelFlag, 0)
                            .last("LIMIT 1");
                    return configMapper.selectOne(wrapper);
                },
                config -> {
                    cacheConfig(configKey, config);
                    return null;
                });
    }

    @Override
    public String getConfigValueFromCache(String configKey) {
        return getFromCacheWithRedis(configKey,
                "configValue",
                prefix -> prefix + configKey,
                key -> {
                    LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Config::getConfigKey, key)
                            .eq(Config::getStatus, 1)
                            .eq(Config::getDelFlag, 0)
                            .last("LIMIT 1");
                    Config config = configMapper.selectOne(wrapper);
                    return config != null ? config.getConfigValue() : null;
                },
                value -> {
                    cacheConfigValue(configKey, value);
                    return null;
                });
    }

    /**
     * 写入 Redis 缓存（异步）
     */
    @Async
    @Override
    public void cacheConfig(String configKey, Config config) {
        if (isCacheEnabled() || config == null) {
            return;
        }

        try {
            // 写入 Caffeine 和 Redis
            cacheData("config", prefix -> prefix + configKey, config);

            log.debug("缓存配置: configKey={}", configKey);
        } catch (Exception e) {
            log.error("缓存配置失败: configKey={}", configKey, e);
        }
    }

    /**
     * 写入 Redis 缓存（异步）
     */
    @Async
    @Override
    public void cacheConfigValue(String configKey, String configValue) {
        if (isCacheEnabled() || configValue == null) {
            return;
        }

        try {
            // 写入 Caffeine 和 Redis
            cacheData("configValue", prefix -> prefix + "value:" + configKey, configValue);

            log.debug("缓存配置值: configKey={}", configKey);
        } catch (Exception e) {
            log.error("缓存配置值失败: configKey={}", configKey, e);
        }
    }

    @Override
    public void evictConfigCache(String configKey) {
        evictCache("config", prefix -> prefix + configKey);
        // 同时清除配置值缓存
        evictConfigValueCache(configKey);
        log.info("清除配置缓存: configKey={}", configKey);
    }

    @Override
    public void evictConfigValueCache(String configKey) {
        evictCache("configValue", prefix -> prefix + "value:" + configKey);
        log.info("清除配置值缓存: configKey={}", configKey);
    }

    @Override
    public void evictAllConfigCache() {
        evictAllCache("config", "configValue");
        log.info("清除所有配置缓存");
    }

    @Override
    public void warmUpCache() {
        if (!cacheProperties.isEnabled()) {
            log.info("系统配置缓存未启用，跳过预热");
            return;
        }

        log.info("开始预热系统配置缓存");

        try {
            // 获取所有启用的配置
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getStatus, 1)
                    .eq(Config::getDelFlag, 0);
            List<Config> configList = configMapper.selectList(wrapper);

            if (configList.isEmpty()) {
                log.info("没有配置需要预热缓存");
                return;
            }

            // 并行预热配置缓存（使用虚拟线程）
            List<CompletableFuture<Void>> futures = configList.stream()
                    .map(config -> CompletableFuture.runAsync(() -> {
                        try {
                            cacheConfig(config.getConfigKey(), config);
                            cacheConfigValue(config.getConfigKey(), config.getConfigValue());
                        } catch (Exception e) {
                            log.error("预热配置缓存失败: configKey={}", config.getConfigKey(), e);
                        }
                    }, virtualThreadExecutor))
                    .toList();

            // 等待所有预热任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("系统配置缓存预热完成, 配置数={}", configList.size());
        } catch (Exception e) {
            log.error("预热系统配置缓存失败", e);
        }
    }
}