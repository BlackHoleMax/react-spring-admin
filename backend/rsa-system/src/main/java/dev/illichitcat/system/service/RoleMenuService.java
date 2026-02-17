package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.RoleMenu;

import java.util.List;

/**
 * 角色菜单关联服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /**
     * 保存角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean saveRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 根据角色ID删除角色菜单关联
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteByRoleId(Long roleId);

    /**
     * 根据菜单ID删除角色菜单关联
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    boolean deleteByMenuId(Long menuId);
}