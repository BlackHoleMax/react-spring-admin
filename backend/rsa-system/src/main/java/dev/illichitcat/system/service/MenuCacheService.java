package dev.illichitcat.system.service;

import dev.illichitcat.system.model.entity.Menu;

import java.util.List;

/**
 * 菜单缓存服务接口
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
public interface MenuCacheService {

    /**
     * 获取用户菜单缓存（Cache-Aside 模式）
     * 1. 先查 Caffeine 一级缓存
     * 2. 未命中查 Redis 二级缓存
     * 3. 都未命中查数据库并回填缓存
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> getUserMenusFromCache(Long userId);

    /**
     * 获取角色菜单缓存
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> getRoleMenusFromCache(Long roleId);

    /**
     * 缓存用户菜单
     *
     * @param userId 用户ID
     * @param menus  菜单列表
     */
    void cacheUserMenus(Long userId, List<Menu> menus);

    /**
     * 缓存角色菜单
     *
     * @param roleId 角色ID
     * @param menus  菜单列表
     */
    void cacheRoleMenus(Long roleId, List<Menu> menus);

    /**
     * 清除用户菜单缓存
     *
     * @param userId 用户ID
     */
    void evictUserMenusCache(Long userId);

    /**
     * 清除角色菜单缓存
     *
     * @param roleId 角色ID
     */
    void evictRoleMenusCache(Long roleId);

    /**
     * 清除所有菜单缓存
     */
    void evictAllMenuCache();

    /**
     * 预热缓存
     */
    void warmUpCache();
}