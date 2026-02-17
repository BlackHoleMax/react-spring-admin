package dev.illichitcat.system.service.impl;

import dev.illichitcat.common.common.constant.SystemConstants;
import dev.illichitcat.system.config.RoleCacheProperties;
import dev.illichitcat.system.dao.mapper.RoleMapper;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色缓存服务实现
 * 采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存 + 异步刷新
 *
 * @author Illichitcat
 * @since 2026/01/10
 */
@Slf4j
@Service
public class RoleCacheServiceImpl extends BaseCacheService implements RoleCacheService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private RolePermService rolePermService;

    @Autowired
    @Lazy
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleCacheProperties cacheProperties;

    @Override
    protected boolean isCacheEnabled() {
        return cacheProperties.isEnabled();
    }

    @Override
    protected boolean isCaffeineEnabled() {
        return cacheProperties.getCaffeine().isEnabled();
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
            case "role" -> cacheProperties.getRolePrefix();
            case "rolePerms" -> cacheProperties.getRolePermsPrefix();
            case "roleMenus" -> cacheProperties.getRoleMenusPrefix();
            case "userRoles" -> cacheProperties.getUserRolesPrefix();
            default -> "";
        };
    }

    @Override
    public Role getRoleFromCache(Long id) {
        return getFromCacheWithRedis(id,
                "role",
                rolePrefix -> rolePrefix + id,
                roleId -> roleService.getById(roleId),
                role -> {
                    cacheRole(role);
                    return null;
                });
    }

    @Override
    public Role getRoleFromCache(String roleCode) {
        return getFromCacheWithRedis(roleCode,
                "role",
                rolePrefix -> rolePrefix + "code:" + roleCode,
                code -> roleService.lambdaQuery().eq(Role::getCode, code).one(),
                role -> {
                    cacheRole(role, true);
                    return null;
                });
    }

    @Override
    public List<Long> getRolePermsFromCache(Long roleId) {
        return getFromCacheWithRedis(roleId,
                "rolePerms",
                prefix -> prefix + roleId,
                rolePermService::selectPermIdsByRoleId,
                permIds -> {
                    cacheRolePerms(roleId, permIds);
                    return null;
                });
    }

    @Override
    public List<Long> getRoleMenusFromCache(Long roleId) {
        return getFromCacheWithRedis(roleId,
                "roleMenus",
                prefix -> prefix + roleId,
                roleMenuService::selectMenuIdsByRoleId,
                menuIds -> {
                    cacheRoleMenus(roleId, menuIds);
                    return null;
                });
    }

    @Override
    public List<Role> getUserRolesFromCache(Long userId) {
        return getFromCacheWithRedis(userId,
                "userRoles",
                prefix -> prefix + userId,
                roleMapper::selectRolesByUserId,
                roles -> {
                    cacheUserRoles(userId, roles);
                    return null;
                });
    }

    @Override
    public void cacheRole(Role role) {
        cacheRole(role, false);
    }

    /**
     * 缓存角色
     */
    private void cacheRole(Role role, boolean skipRedis) {
        if (!isCacheEnabled() || role == null) {
            return;
        }

        // 写入 Caffeine 一级缓存
        putToCaffeine("role", String.valueOf(role.getId()), role);
        putToCaffeine("role", "code:" + role.getCode(), role);

        // 写入 Redis 二级缓存
        if (isRedisEnabled() && !skipRedis) {
            asyncPutToRedis(getRedisPrefixByCacheName("role") + role.getId(), role);
            asyncPutToRedis(getRedisPrefixByCacheName("role") + "code:" + role.getCode(), role);
        }

        log.debug("缓存角色成功, roleId={}, roleCode={}", role.getId(), role.getCode());
    }

    @Override
    public void cacheRolePerms(Long roleId, List<Long> permIds) {
        if (!isCacheEnabled() || roleId == null || permIds == null) {
            return;
        }

        // 写入 Caffeine 和 Redis
        cacheData("rolePerms", prefix -> prefix + roleId, permIds);

        log.debug("缓存角色权限成功, roleId={}, permCount={}", roleId, permIds.size());
    }

    @Override
    public void cacheRoleMenus(Long roleId, List<Long> menuIds) {
        if (!isCacheEnabled() || roleId == null || menuIds == null) {
            return;
        }

        // 写入 Caffeine 和 Redis
        cacheData("roleMenus", prefix -> prefix + roleId, menuIds);

        log.debug("缓存角色菜单成功, roleId={}, menuCount={}", roleId, menuIds.size());
    }

    @Override
    public void cacheUserRoles(Long userId, List<Role> roles) {
        if (!isCacheEnabled() || userId == null || roles == null) {
            return;
        }

        // 写入 Caffeine 和 Redis
        cacheData("userRoles", prefix -> prefix + userId, roles);

        log.debug("缓存用户角色成功, userId={}, roleCount={}", userId, roles.size());
    }

    @Override
    public void evictRoleCache(Long id) {
        evictCache("role", prefix -> prefix + id);
        log.info("清除角色缓存成功, roleId={}", id);
    }

    @Override
    public void evictRoleCache(String roleCode) {
        evictCache("role", prefix -> prefix + "code:" + roleCode);
        log.info("清除角色缓存成功, roleCode={}", roleCode);
    }

    @Override
    public void evictRolePermsCache(Long roleId) {
        evictCache("rolePerms", prefix -> prefix + roleId);
        log.info("清除角色权限缓存成功, roleId={}", roleId);
    }

    @Override
    public void evictRoleMenusCache(Long roleId) {
        evictCache("roleMenus", prefix -> prefix + roleId);
        log.info("清除角色菜单缓存成功, roleId={}", roleId);
    }

    @Override
    public void evictUserRolesCache(Long userId) {
        evictCache("userRoles", prefix -> prefix + userId);
        log.info("清除用户角色缓存成功, userId={}", userId);
    }

    @Override
    public void evictAllRoleCache() {
        evictAllCache("role", "rolePerms", "roleMenus", "userRoles");
        log.info("清除所有角色缓存成功");
    }

    @Override
    public void warmUpCache() {
        if (!cacheProperties.isEnabled()) {
            return;
        }
        log.info("开始预热角色缓存, source={}", SystemConstants.CacheSource.MANUAL);

        List<Role> roleList = roleService.list();
        for (Role role : roleList) {
            try {
                cacheRole(role);
                List<Long> permIds = rolePermService.selectPermIdsByRoleId(role.getId());
                cacheRolePerms(role.getId(), permIds);
                List<Long> menuIds = roleMenuService.selectMenuIdsByRoleId(role.getId());
                cacheRoleMenus(role.getId(), menuIds);
            } catch (Exception e) {
                log.error("预热角色缓存失败, roleId={}, roleCode={}", role.getId(), role.getCode(), e);
            }
        }

        log.info("角色缓存预热完成, 总角色数={}, source={}", roleList.size(), SystemConstants.CacheSource.MANUAL);
    }
}