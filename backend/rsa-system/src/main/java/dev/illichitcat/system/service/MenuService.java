package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.MenuExcelDTO;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.model.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查询所有菜单列表
     *
     * @param menu 查询条件
     * @return 菜单列表
     */
    List<Menu> selectMenuList(Menu menu);

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    Menu selectMenuById(Long id);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean insertMenu(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean updateMenu(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean deleteMenuById(Long id);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectMenusByUserId(Long userId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> selectMenusByRoleId(Long roleId);

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    List<MenuVO> buildMenuTree(List<Menu> menus);

    /**
     * 导出菜单数据到Excel
     *
     * @param menuIds 菜单ID列表，为空则导出所有
     * @return 菜单Excel数据列表
     */
    List<MenuExcelDTO> exportMenus(List<Long> menuIds);

    /**
     * 从Excel导入菜单数据
     *
     * @param menuExcelDTOList 菜单Excel数据列表
     * @return 导入结果信息
     */
    String importMenus(List<MenuExcelDTO> menuExcelDTOList);

    /**
     * 批量删除菜单
     *
     * @param menuIds 菜单ID数组
     * @return 删除结果
     */
    boolean deleteMenusByIds(Long[] menuIds);
}