package dev.illichitcat.system.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 *
 * @author Illichitcat
 * @since 2025/12/30
 */
@Slf4j
@Configuration
@EnableCaching
public class CaffeineConfig {

    /**
     * 时间单位：分钟
     */
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    @Bean
    public CacheManager cacheManager(DictCacheProperties dictCacheProperties, RoleCacheProperties roleCacheProperties, MenuCacheProperties menuCacheProperties, PermissionCacheProperties permissionCacheProperties, ConfigCacheProperties configCacheProperties) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>(16);

        // 配置各种类型的缓存
        caches.addAll(configureDictCaches(dictCacheProperties));
        caches.addAll(configureRoleCaches(roleCacheProperties));
        caches.addAll(configureMenuCaches(menuCacheProperties));
        caches.addAll(configurePermissionCaches(permissionCacheProperties));
        caches.addAll(configureConfigCaches(configCacheProperties));

        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 配置字典缓存
     *
     * @param dictCacheProperties 字典缓存配置属性
     * @return 字典缓存列表
     */
    private List<CaffeineCache> configureDictCaches(DictCacheProperties dictCacheProperties) {
        List<CaffeineCache> caches = new ArrayList<>(3);
        DictCacheProperties.Caffeine dictCaffeineConfig = dictCacheProperties.getCaffeine();

        if (dictCaffeineConfig.isEnabled()) {
            Caffeine<Object, Object> caffeineBuilder = buildCaffeine(dictCaffeineConfig);

            caches.add(new CaffeineCache("dict", caffeineBuilder.build()));
            caches.add(new CaffeineCache("dictItem", caffeineBuilder.build()));
            caches.add(new CaffeineCache("dictItems", caffeineBuilder.build()));

            log.info("Caffeine 字典缓存已启用: initialCapacity={}, maximumSize={}, expireAfterWrite={}min, expireAfterAccess={}min",
                    dictCaffeineConfig.getInitialCapacity(),
                    dictCaffeineConfig.getMaximumSize(),
                    dictCaffeineConfig.getExpireAfterWrite(),
                    dictCaffeineConfig.getExpireAfterAccess());
        } else {
            log.info("Caffeine 字典缓存已禁用");
        }

        return caches;
    }

    /**
     * 配置角色缓存
     *
     * @param roleCacheProperties 角色缓存配置属性
     * @return 角色缓存列表
     */
    private List<CaffeineCache> configureRoleCaches(RoleCacheProperties roleCacheProperties) {
        List<CaffeineCache> caches = new ArrayList<>(4);
        RoleCacheProperties.Caffeine roleCaffeineConfig = roleCacheProperties.getCaffeine();

        if (roleCaffeineConfig.isEnabled()) {
            Caffeine<Object, Object> caffeineBuilder = buildCaffeine(roleCaffeineConfig);

            caches.add(new CaffeineCache("role", caffeineBuilder.build()));
            caches.add(new CaffeineCache("rolePerms", caffeineBuilder.build()));
            caches.add(new CaffeineCache("roleMenus", caffeineBuilder.build()));
            caches.add(new CaffeineCache("userRoles", caffeineBuilder.build()));

            log.info("Caffeine 角色缓存已启用: initialCapacity={}, maximumSize={}, expireAfterWrite={}min, expireAfterAccess={}min",
                    roleCaffeineConfig.getInitialCapacity(),
                    roleCaffeineConfig.getMaximumSize(),
                    roleCaffeineConfig.getExpireAfterWrite(),
                    roleCaffeineConfig.getExpireAfterAccess());
        } else {
            log.info("Caffeine 角色缓存已禁用");
        }

        return caches;
    }

    /**
     * 配置菜单缓存
     *
     * @param menuCacheProperties 菜单缓存配置属性
     * @return 菜单缓存列表
     */
    private List<CaffeineCache> configureMenuCaches(MenuCacheProperties menuCacheProperties) {
        List<CaffeineCache> caches = new ArrayList<>(2);
        MenuCacheProperties.Caffeine menuCaffeineConfig = menuCacheProperties.getCaffeine();

        if (menuCaffeineConfig.isEnabled()) {
            Caffeine<Object, Object> caffeineBuilder = buildCaffeine(menuCaffeineConfig);

            caches.add(new CaffeineCache("userMenus", caffeineBuilder.build()));
            caches.add(new CaffeineCache("roleMenus", caffeineBuilder.build()));

            log.info("Caffeine 菜单缓存已启用: initialCapacity={}, maximumSize={}, expireAfterWrite={}min, expireAfterAccess={}min",
                    menuCaffeineConfig.getInitialCapacity(),
                    menuCaffeineConfig.getMaximumSize(),
                    menuCaffeineConfig.getExpireAfterWrite(),
                    menuCaffeineConfig.getExpireAfterAccess());
        } else {
            log.info("Caffeine 菜单缓存已禁用");
        }

        return caches;
    }

    /**
     * 配置权限缓存
     *
     * @param permissionCacheProperties 权限缓存配置属性
     * @return 权限缓存列表
     */
    private List<CaffeineCache> configurePermissionCaches(PermissionCacheProperties permissionCacheProperties) {
        List<CaffeineCache> caches = new ArrayList<>(3);
        PermissionCacheProperties.Caffeine permissionCaffeineConfig = permissionCacheProperties.getCaffeine();

        if (permissionCaffeineConfig.isEnabled()) {
            Caffeine<Object, Object> caffeineBuilder = buildCaffeine(permissionCaffeineConfig);

            caches.add(new CaffeineCache("userPermissions", caffeineBuilder.build()));
            caches.add(new CaffeineCache("userPerms", caffeineBuilder.build()));
            caches.add(new CaffeineCache("rolePermissions", caffeineBuilder.build()));

            log.info("Caffeine 权限缓存已启用: initialCapacity={}, maximumSize={}, expireAfterWrite={}min, expireAfterAccess={}min",
                    permissionCaffeineConfig.getInitialCapacity(),
                    permissionCaffeineConfig.getMaximumSize(),
                    permissionCaffeineConfig.getExpireAfterWrite(),
                    permissionCaffeineConfig.getExpireAfterAccess());
        } else {
            log.info("Caffeine 权限缓存已禁用");
        }

        return caches;
    }

    /**
     * 配置系统配置缓存
     *
     * @param configCacheProperties 配置缓存属性
     * @return 配置缓存列表
     */
    private List<CaffeineCache> configureConfigCaches(ConfigCacheProperties configCacheProperties) {
        List<CaffeineCache> caches = new ArrayList<>(2);
        ConfigCacheProperties.Caffeine configCaffeineConfig = configCacheProperties.getCaffeine();

        if (configCaffeineConfig.isEnabled()) {
            Caffeine<Object, Object> caffeineBuilder = buildCaffeine(configCaffeineConfig);

            caches.add(new CaffeineCache("config", caffeineBuilder.build()));
            caches.add(new CaffeineCache("configValue", caffeineBuilder.build()));

            log.info("Caffeine 系统配置缓存已启用: initialCapacity={}, maximumSize={}, expireAfterWrite={}min, expireAfterAccess={}min",
                    configCaffeineConfig.getInitialCapacity(),
                    configCaffeineConfig.getMaximumSize(),
                    configCaffeineConfig.getExpireAfterWrite(),
                    configCaffeineConfig.getExpireAfterAccess());
        } else {
            log.info("Caffeine 系统配置缓存已禁用");
        }

        return caches;
    }

    /**
     * 构建 Caffeine 缓存构建器
     * 使用反射来支持不同类型的缓存配置类
     *
     * @param caffeineConfig Caffeine 配置对象（DictCacheProperties.Caffeine、RoleCacheProperties.Caffeine 等）
     * @return Caffeine 构建器
     */
    private Caffeine<Object, Object> buildCaffeine(Object caffeineConfig) {
        try {
            // 使用反射获取配置字段
            boolean isEnabled = (boolean) caffeineConfig.getClass().getMethod("isEnabled").invoke(caffeineConfig);
            if (!isEnabled) {
                return null;
            }

            int initialCapacity = (int) caffeineConfig.getClass().getMethod("getInitialCapacity").invoke(caffeineConfig);
            long maximumSize = (long) caffeineConfig.getClass().getMethod("getMaximumSize").invoke(caffeineConfig);
            long expireAfterWrite = (long) caffeineConfig.getClass().getMethod("getExpireAfterWrite").invoke(caffeineConfig);
            long expireAfterAccess = (long) caffeineConfig.getClass().getMethod("getExpireAfterAccess").invoke(caffeineConfig);
            boolean recordStats = (boolean) caffeineConfig.getClass().getMethod("isRecordStats").invoke(caffeineConfig);

            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                    .initialCapacity(initialCapacity)
                    .maximumSize(maximumSize)
                    .expireAfterWrite(expireAfterWrite, TIME_UNIT)
                    .expireAfterAccess(expireAfterAccess, TIME_UNIT);

            if (recordStats) {
                builder.recordStats();
            }

            return builder;
        } catch (Exception e) {
            log.error("构建 Caffeine 配置失败", e);
            throw new RuntimeException("构建 Caffeine 配置失败", e);
        }
    }
}