package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.illichitcat.common.common.constant.SystemConstants;
import dev.illichitcat.system.config.MenuCacheProperties;
import dev.illichitcat.system.dao.mapper.MenuMapper;
import dev.illichitcat.system.dao.mapper.RoleMenuMapper;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.service.BaseCacheService;
import dev.illichitcat.system.service.MenuCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单缓存服务实现
 * 采用 Cache-Aside 模式 + Caffeine 一级缓存 + Redis 二级缓存 + 异步刷新
 * 继承 BaseCacheService 消除重复代码
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Service
public class MenuCacheServiceImpl extends BaseCacheService implements MenuCacheService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MenuCacheProperties cacheProperties;

    @Autowired
    private ObjectMapper objectMapper;

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
            case "userMenus" -> cacheProperties.getUserMenusPrefix();
            case "roleMenus" -> cacheProperties.getRoleMenusPrefix();
            default -> "";
        };
    }

    @Override
    public List<Menu> getUserMenusFromCache(Long userId) {
        if (!isCacheEnabled()) {
            return selectMenusByUserIdFromDb(userId);
        }
        return getMenusFromCache("userMenus", userId, this::selectMenusByUserIdFromDb, this::cacheUserMenus);
    }

    @Override
    public List<Menu> getRoleMenusFromCache(Long roleId) {
        if (!isCacheEnabled()) {
            return selectMenusByRoleIdFromDb(roleId);
        }
        return getMenusFromCache("roleMenus", roleId, this::selectMenusByRoleIdFromDb, this::cacheRoleMenus);
    }

    @Override
    public void cacheUserMenus(Long userId, List<Menu> menus) {
        cacheData("userMenus", prefix -> cacheProperties.getMenuPrefix() + userId, menus);
        log.debug("缓存用户菜单成功, userId={}, menuCount={}", userId, menus.size());
    }

    @Override
    public void cacheRoleMenus(Long roleId, List<Menu> menus) {
        cacheData("roleMenus", prefix -> cacheProperties.getMenuPrefix() + roleId, menus);
        log.debug("缓存角色菜单成功, roleId={}, menuCount={}", roleId, menus.size());
    }

    @Override
    public void evictUserMenusCache(Long userId) {
        evictCache("userMenus", prefix -> cacheProperties.getMenuPrefix() + userId);
        log.debug("清除用户菜单缓存成功, userId={}", userId);
    }

    @Override
    public void evictRoleMenusCache(Long roleId) {
        evictCache("roleMenus", prefix -> cacheProperties.getMenuPrefix() + roleId);
        log.debug("清除角色菜单缓存成功, roleId={}", roleId);
    }

    @Override
    public void evictAllMenuCache() {
        evictAllCache("userMenus", "roleMenus");
    }

    @Override
    public void warmUpCache() {
        if (!isCacheEnabled()) {
            return;
        }
        log.info("开始预热菜单缓存, source={}", SystemConstants.CacheSource.MANUAL);

        // 预热角色菜单缓存
        List<Long> roleIds = roleMenuMapper.selectDistinctRoleIds();
        for (Long roleId : roleIds) {
            try {
                List<Menu> menus = selectMenusByRoleIdFromDb(roleId);
                cacheRoleMenus(roleId, menus);
            } catch (Exception e) {
                log.error("预热角色菜单缓存失败, roleId={}", roleId, e);
            }
        }

        log.info("菜单缓存预热完成, 角色数={}, source={}", roleIds.size(), SystemConstants.CacheSource.MANUAL);
    }

    /**
     * 清除用户菜单缓存（当用户角色变更时调用）
     */
    public void evictUserMenusCacheByRoleIds(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            return;
        }
        evictUserMenusCache(userId);
        log.debug("清除用户菜单缓存成功, userId={}, roleIds={}", userId, roleIds);
    }

    /**
     * 从数据库查询用户菜单（避免循环依赖）
     */
    private List<Menu> selectMenusByUserIdFromDb(Long userId) {
        // 查询用户的角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }

        // 查询角色关联的菜单ID列表
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleIds(roleIds);
        if (menuIds.isEmpty()) {
            return List.of();
        }

        // 查询菜单列表
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Menu::getId, menuIds)
                .eq(Menu::getStatus, 1)
                .eq(Menu::getDelFlag, 0)
                .orderByAsc(Menu::getSort);
        return menuMapper.selectList(wrapper);
    }

    /**
     * 从数据库查询角色菜单（避免循环依赖）
     */
    private List<Menu> selectMenusByRoleIdFromDb(Long roleId) {
        // 查询角色关联的菜单ID列表
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds.isEmpty()) {
            return List.of();
        }

        // 查询菜单列表
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Menu::getId, menuIds)
                .eq(Menu::getStatus, 1)
                .eq(Menu::getDelFlag, 0)
                .orderByAsc(Menu::getSort);
        return menuMapper.selectList(wrapper);
    }

    /**
     * 从缓存获取菜单（通用方法）
     *
     * @param cacheName     缓存名称
     * @param id            ID
     * @param dbLoader      数据库加载器
     * @param cacheOperator 缓存操作器
     * @return 菜单列表
     */
    private List<Menu> getMenusFromCache(String cacheName, Long id,
                                         java.util.function.Function<Long, List<Menu>> dbLoader,
                                         java.util.function.BiConsumer<Long, List<Menu>> cacheOperator) {
        // 1. 先查 Caffeine 一级缓存
        List<Menu> value = getFromCaffeine(cacheName, cacheProperties.getMenuPrefix() + id);
        if (value != null) {
            log.debug("Caffeine 命中: cacheName={}, key={}", cacheName, id);
            return value;
        }

        // 2. 未命中查 Redis 二级缓存
        String redisKey = cacheName.equals("userMenus") ?
                cacheProperties.getUserMenusPrefix() + id :
                cacheProperties.getRoleMenusPrefix() + id;
        String rawValue = getFromRedisRaw(redisKey);
        if (rawValue != null) {
            log.debug("Redis 命中: cacheName={}, key={}", cacheName, id);
            try {
                // 反序列化为 List<Menu>
                List<Menu> menus = objectMapper.readValue(rawValue,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Menu.class));
                // 回填 Caffeine 缓存
                putToCaffeine(cacheName, cacheProperties.getMenuPrefix() + id, menus);
                return menus;
            } catch (Exception e) {
                log.error("反序列化 Redis 缓存失败, key={}", id, e);
            }
        }

        // 3. 都未命中查数据库
        log.debug("缓存未命中，查询数据库: cacheName={}, key={}", cacheName, id);
        List<Menu> dbValue = dbLoader.apply(id);
        if (dbValue != null) {
            // 回填缓存
            cacheOperator.accept(id, dbValue);
        }

        return dbValue;
    }

}