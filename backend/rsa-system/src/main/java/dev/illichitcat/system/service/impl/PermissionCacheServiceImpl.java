package dev.illichitcat.system.service.impl;

import dev.illichitcat.system.config.PermissionCacheProperties;
import dev.illichitcat.system.dao.mapper.PermissionMapper;
import dev.illichitcat.system.dao.mapper.RolePermMapper;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.service.BaseCacheService;
import dev.illichitcat.system.service.PermissionCacheService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * 权限缓存服务实现
 * 采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存 + 异步刷新
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Service
public class PermissionCacheServiceImpl extends BaseCacheService implements PermissionCacheService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Resource(name = "virtualThreadExecutor")
    private ExecutorService virtualThreadExecutor;

    @Autowired
    private RolePermMapper rolePermMapper;

    @Autowired
    private PermissionCacheProperties cacheProperties;

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
            case "userPermissions" ->
                    cacheProperties.getPermissionPrefix() + cacheProperties.getUserPermissionsPrefix();
            case "userPerms" -> cacheProperties.getPermissionPrefix() + cacheProperties.getUserPermsPrefix();
            case "rolePermissions" ->
                    cacheProperties.getPermissionPrefix() + cacheProperties.getRolePermissionsPrefix();
            default -> cacheProperties.getPermissionPrefix();
        };
    }

    @Override
    public List<Permission> getUserPermissionsFromCache(Long userId) {
        return getFromCache(userId,
                "userPermissions",
                prefix -> prefix + userId,
                key -> permissionMapper.selectPermissionsByUserId(key),
                permissions -> {
                    cacheUserPermissions(userId, permissions);
                    return null;
                });
    }

    @Override
    public List<String> getUserPermsFromCache(Long userId) {
        return getFromCache(userId,
                "userPerms",
                prefix -> prefix + userId,
                key -> permissionMapper.selectPermsByUserId(key),
                perms -> {
                    cacheUserPerms(userId, perms);
                    return null;
                });
    }

    @Override
    public List<Permission> getRolePermissionsFromCache(Long roleId) {
        return getFromCache(roleId,
                "rolePermissions",
                prefix -> prefix + roleId,
                key -> permissionMapper.selectPermissionsByRoleId(key),
                permissions -> {
                    cacheRolePermissions(roleId, permissions);
                    return null;
                });
    }

    /**
     * 通用缓存获取方法（Cache-Aside 模式）
     */
    @Override
    @SuppressWarnings("unchecked")
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
            log.debug("Caffeine 命中: cacheName={}, key={}", cacheName, key);
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        List<Object> redisValue = getFromRedisAsList(keyGenerator.apply(getRedisPrefixByCacheName(cacheName)));
        if (redisValue != null && !redisValue.isEmpty()) {
            value = (V) redisValue;
            log.debug("Redis 命中: cacheName={}, key={}", cacheName, key);
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
     * 写入 Redis 缓存（异步）
     */
    @Async
    @Override
    public void cacheUserPermissions(Long userId, List<Permission> permissions) {
        if (isCacheEnabled() || permissions == null) {
            return;
        }

        try {
            String key = cacheProperties.getUserPermissionsPrefix() + userId;
            // 写入 Caffeine 和 Redis
            cacheData("userPermissions", prefix -> prefix + key, permissions);

            log.debug("缓存用户权限: userId={}, count={}", userId, permissions.size());
        } catch (Exception e) {
            log.error("缓存用户权限失败: userId={}", userId, e);
        }
    }

    /**
     * 写入 Redis 缓存（异步）
     */
    @Async
    @Override
    public void cacheUserPerms(Long userId, List<String> perms) {
        if (isCacheEnabled() || perms == null) {
            return;
        }

        try {
            String key = cacheProperties.getUserPermsPrefix() + userId;
            // 写入 Caffeine 和 Redis
            cacheData("userPerms", prefix -> prefix + key, perms);

            log.debug("缓存用户权限标识: userId={}, count={}", userId, perms.size());
        } catch (Exception e) {
            log.error("缓存用户权限标识失败: userId={}", userId, e);
        }
    }

    /**
     * 写入 Redis 缓存（异步）
     */
    @Async
    @Override
    public void cacheRolePermissions(Long roleId, List<Permission> permissions) {
        if (isCacheEnabled() || permissions == null) {
            return;
        }

        try {
            String key = cacheProperties.getRolePermissionsPrefix() + roleId;
            // 写入 Caffeine 和 Redis
            cacheData("rolePermissions", prefix -> prefix + key, permissions);

            log.debug("缓存角色权限: roleId={}, count={}", roleId, permissions.size());
        } catch (Exception e) {
            log.error("缓存角色权限失败: roleId={}", roleId, e);
        }
    }

    @Override
    public void evictUserPermissionsCache(Long userId) {
        String key = cacheProperties.getUserPermissionsPrefix() + userId;
        evictCache("userPermissions", prefix -> prefix + key);

        // 同时清除用户权限标识缓存
        evictUserPermsCache(userId);
    }

    @Override
    public void evictUserPermsCache(Long userId) {
        String key = cacheProperties.getUserPermsPrefix() + userId;
        evictCache("userPerms", prefix -> prefix + key);
    }

    @Override
    public void evictRolePermissionsCache(Long roleId) {
        String key = cacheProperties.getRolePermissionsPrefix() + roleId;
        evictCache("rolePermissions", prefix -> prefix + key);
    }

    @Override
    public void evictAllPermissionCache() {
        evictAllCache("userPermissions", "userPerms", "rolePermissions");
    }

    @Override
    public void warmUpCache() {
        if (!cacheProperties.isEnabled()) {
            log.info("权限缓存未启用，跳过预热");
            return;
        }

        log.info("开始预热权限缓存");

        try {
            // 获取所有角色ID
            List<Long> roleIds = rolePermMapper.selectDistinctRoleIds();
            if (roleIds.isEmpty()) {
                log.info("没有角色需要预热缓存");
                return;
            }

            // 并行预热角色权限缓存（使用虚拟线程）
            List<CompletableFuture<Void>> futures = roleIds.stream()
                    .map(roleId -> CompletableFuture.runAsync(() -> {
                        try {
                            List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(roleId);
                            if (permissions != null && !permissions.isEmpty()) {
                                cacheRolePermissions(roleId, permissions);
                            }
                        } catch (Exception e) {
                            log.error("预热角色权限缓存失败: roleId={}", roleId, e);
                        }
                    }, virtualThreadExecutor))
                    .toList();

            // 等待所有预热任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("权限缓存预热完成, 角色数={}", roleIds.size());
        } catch (Exception e) {
            log.error("预热权限缓存失败", e);
        }
    }
}