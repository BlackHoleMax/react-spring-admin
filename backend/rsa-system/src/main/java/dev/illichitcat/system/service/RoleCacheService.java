package dev.illichitcat.system.service;

import dev.illichitcat.system.model.entity.Role;

import java.util.List;

/**
 * 角色缓存服务接口
 *
 * @author Illichitcat
 * @since 2026/01/10
 */
public interface RoleCacheService {

    /**
     * 获取角色缓存（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存
     * 3. 都未命中查数据库并回填缓存
     *
     * @param id 角色ID
     * @return 角色对象
     */
    Role getRoleFromCache(Long id);

    /**
     * 获取角色缓存（通过编码）
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    Role getRoleFromCache(String roleCode);

    /**
     * 获取角色权限ID列表缓存
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermsFromCache(Long roleId);

    /**
     * 获取角色菜单ID列表缓存
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenusFromCache(Long roleId);

    /**
     * 获取用户角色列表缓存
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRolesFromCache(Long userId);

    /**
     * 缓存角色
     *
     * @param role 角色对象
     */
    void cacheRole(Role role);

    /**
     * 缓存角色权限ID列表
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     */
    void cacheRolePerms(Long roleId, List<Long> permIds);

    /**
     * 缓存角色菜单ID列表
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void cacheRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 缓存用户角色列表
     *
     * @param userId 用户ID
     * @param roles  角色列表
     */
    void cacheUserRoles(Long userId, List<Role> roles);

    /**
     * 清除角色缓存
     *
     * @param id 角色ID
     */
    void evictRoleCache(Long id);

    /**
     * 清除角色缓存（通过编码）
     *
     * @param roleCode 角色编码
     */
    void evictRoleCache(String roleCode);

    /**
     * 清除角色权限缓存
     *
     * @param roleId 角色ID
     */
    void evictRolePermsCache(Long roleId);

    /**
     * 清除角色菜单缓存
     *
     * @param roleId 角色ID
     */
    void evictRoleMenusCache(Long roleId);

    /**
     * 清除用户角色缓存
     *
     * @param userId 用户ID
     */
    void evictUserRolesCache(Long userId);

    /**
     * 清除所有角色缓存
     */
    void evictAllRoleCache();

    /**
     * 预热缓存
     */
    void warmUpCache();
}