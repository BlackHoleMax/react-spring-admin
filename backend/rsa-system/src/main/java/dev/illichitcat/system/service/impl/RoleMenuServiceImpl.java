package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.RoleMenuMapper;
import dev.illichitcat.system.model.entity.RoleMenu;
import dev.illichitcat.system.service.RoleCacheService;
import dev.illichitcat.system.service.RoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色菜单关联服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleCacheService roleCacheService;

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleCacheService.getRoleMenusFromCache(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRoleMenus(Long roleId, List<Long> menuIds) {
        log.info("保存角色菜单关联: roleId={}, menuIds={}", roleId, menuIds);
        // 先删除原有关联
        roleMenuMapper.deleteByRoleId(roleId);
        // 批量插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = new ArrayList<>();
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
            }
            boolean result = this.saveBatch(roleMenus);
            if (result) {
                // 保存成功后，缓存角色菜单
                roleCacheService.cacheRoleMenus(roleId, menuIds);
            }
            return result;
        }
        return true;
    }

    @Override
    public boolean deleteByRoleId(Long roleId) {
        log.info("删除角色菜单关联: roleId={}", roleId);
        boolean result = roleMenuMapper.deleteByRoleId(roleId) >= 0;
        if (result) {
            // 删除成功后，清除缓存
            roleCacheService.evictRoleMenusCache(roleId);
        }
        return result;
    }

    @Override
    public boolean deleteByMenuId(Long menuId) {
        log.info("删除角色菜单关联: menuId={}", menuId);
        return roleMenuMapper.deleteByMenuId(menuId) >= 0;
    }
}
