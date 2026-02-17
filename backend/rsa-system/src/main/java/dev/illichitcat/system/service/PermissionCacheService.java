package dev.illichitcat.system.service;

import dev.illichitcat.system.model.entity.Permission;

import java.util.List;

/**
 * 权限缓存服务接口
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
public interface PermissionCacheService {

    /**
     * 获取用户权限缓存（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存
     * 3. 都未命中查数据库并回填缓存
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissionsFromCache(Long userId);

    /**
     * 获取用户权限标识缓存
     * 从缓存中获取用户的权限标识列表（权限字符串），包括权限标识的编码信息
     * 采用 Cache-Aside 模式：先查 Caffeine 一级缓存，未命中查 Redis 二级缓存，都未命中查数据库并回填缓存
     *
     * @param userId 用户ID，用于标识要查询权限的用户
     * @return 权限标识列表，包含该用户拥有的所有权限标识字符串
     */
    List<String> getUserPermsFromCache(Long userId);

    /**
     * 获取角色权限缓存
     * 从缓存中获取角色的权限列表，采用 Cache-Aside 模式
     * 先查 Caffeine 一级缓存，未命中查 Redis 二级缓存，都未命中查数据库并回填缓存
     *
     * @param roleId 角色ID，用于标识要查询权限的角色
     * @return 权限列表，包含该角色拥有的所有权限对象
     */
    List<Permission> getRolePermissionsFromCache(Long roleId);

    /**
     * 缓存用户权限
     * 将用户的权限列表缓存到 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法会同时更新两级缓存，确保数据一致性
     *
     * @param userId      用户ID，用于标识要缓存权限的用户
     * @param permissions 权限列表，包含该用户拥有的所有权限对象
     */
    void cacheUserPermissions(Long userId, List<Permission> permissions);

    /**
     * 缓存用户权限标识
     * 将用户的权限标识列表（权限字符串）缓存到 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法会同时更新两级缓存，确保数据一致性
     *
     * @param userId 用户ID，用于标识要缓存权限标识的用户
     * @param perms  权限标识列表，包含该用户拥有的所有权限标识字符串
     */
    void cacheUserPerms(Long userId, List<String> perms);

    /**
     * 缓存角色权限
     * 将角色的权限列表缓存到 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法会同时更新两级缓存，确保数据一致性
     *
     * @param roleId      角色ID，用于标识要缓存权限的角色
     * @param permissions 权限列表，包含该角色拥有的所有权限对象
     */
    void cacheRolePermissions(Long roleId, List<Permission> permissions);

    /**
     * 清除用户权限缓存
     * 根据用户ID清除指定的用户权限对象缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法不会清除用户权限标识缓存，如需同时清除，请调用 evictUserPermsCache
     *
     * @param userId 用户ID，用于标识要清除权限的用户
     */
    void evictUserPermissionsCache(Long userId);

    /**
     * 清除用户权限标识缓存
     * 根据用户ID清除指定的用户权限标识缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法不会清除用户权限对象缓存，如需同时清除，请调用 evictUserPermissionsCache
     *
     * @param userId 用户ID，用于标识要清除权限标识的用户
     */
    void evictUserPermsCache(Long userId);

    /**
     * 清除角色权限缓存
     * 根据角色ID清除指定的角色权限缓存，包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法通常在角色权限变更时调用，确保缓存与数据库数据一致
     *
     * @param roleId 角色ID，用于标识要清除权限的角色
     */
    void evictRolePermissionsCache(Long roleId);

    /**
     * 清除所有权限缓存
     * 清除系统中所有的权限缓存，包括用户权限、用户权限标识、角色权限
     * 包括 Caffeine 一级缓存和 Redis 二级缓存
     * 该方法通常在系统权限批量更新或系统重置时调用
     */
    void evictAllPermissionCache();

    /**
     * 预热缓存
     */
    void warmUpCache();
}