package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.system.dao.mapper.MenuMapper;
import dev.illichitcat.system.dao.mapper.PermissionMapper;
import dev.illichitcat.system.dao.mapper.RoleMenuMapper;
import dev.illichitcat.system.model.dto.MenuExcelDTO;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.model.entity.RoleMenu;
import dev.illichitcat.system.model.vo.MenuVO;
import dev.illichitcat.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    @Lazy
    private dev.illichitcat.system.service.MenuCacheService menuCacheService;

    @Override
    public List<Menu> selectMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        if (menu != null) {
            if (menu.getName() != null && !menu.getName().isEmpty()) {
                queryWrapper.like(Menu::getName, menu.getName());
            }
            if (menu.getStatus() != null) {
                queryWrapper.eq(Menu::getStatus, menu.getStatus());
            }
        }
        queryWrapper.orderByAsc(Menu::getSort);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public Menu selectMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public boolean insertMenu(Menu menu) {
        log.info("新增菜单: {}", menu.getName());
        boolean result = menuMapper.insert(menu) > 0;
        if (result) {
            // 清除所有菜单缓存
            menuCacheService.evictAllMenuCache();
        }
        return result;
    }

    @Override
    public boolean updateMenu(Menu menu) {
        log.info("更新菜单: id={}", menu.getId());
        boolean result = menuMapper.updateById(menu) > 0;
        if (result) {
            // 清除所有菜单缓存
            menuCacheService.evictAllMenuCache();
        }
        return result;
    }

    @Override
    public boolean deleteMenuById(Long id) {
        log.info("删除菜单: id={}", id);

        // 检查菜单是否有子菜单
        List<Menu> childMenus = menuMapper.selectList(new QueryWrapper<Menu>().eq("parent_id", id));
        if (!childMenus.isEmpty()) {
            Menu menu = this.getById(id);
            String menuName = menu != null ? menu.getName() : String.valueOf(id);
            log.warn("删除菜单失败，菜单有子菜单, menuId={}, menuName={}", id, menuName);
            throw new BizException("菜单 " + menuName + " 有子菜单，无法删除");
        }

        // 检查菜单是否分配给角色
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("menu_id", id));
        if (!roleMenus.isEmpty()) {
            Menu menu = this.getById(id);
            String menuName = menu != null ? menu.getName() : String.valueOf(id);
            log.warn("删除菜单失败，菜单已分配给角色, menuId={}, menuName={}", id, menuName);
            throw new BizException("菜单 " + menuName + " 已分配给角色，无法删除");
        }

        // 检查菜单是否有关联的权限
        List<Permission> permissions = permissionMapper.selectList(new QueryWrapper<Permission>().eq("menu_id", id));
        if (!permissions.isEmpty()) {
            Menu menu = this.getById(id);
            String menuName = menu != null ? menu.getName() : String.valueOf(id);
            log.warn("删除菜单失败，菜单有关联的权限, menuId={}, menuName={}", id, menuName);
            throw new BizException("菜单 " + menuName + " 有关联的权限，无法删除");
        }

        boolean result = menuMapper.deleteById(id) > 0;
        if (result) {
            // 清除所有菜单缓存
            menuCacheService.evictAllMenuCache();
        }
        return result;
    }

    @Override
    public List<Menu> selectMenusByUserId(Long userId) {
        // 使用缓存获取用户菜单
        List<Menu> userMenus = menuCacheService.getUserMenusFromCache(userId);
        return fillParentMenus(userMenus);
    }

    private List<Menu> fillParentMenus(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return menus;
        }

        Set<Long> menuIds = menus.stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());

        Set<Long> parentIds = menus.stream()
                .map(Menu::getParentId)
                .filter(parentId -> parentId != null && parentId != 0)
                .filter(parentId -> !menuIds.contains(parentId))
                .collect(Collectors.toSet());

        if (parentIds.isEmpty()) {
            // 对结果按 sort 字段排序
            return menus.stream()
                    .sorted(Comparator.comparingInt(menu -> menu.getSort() != null ? menu.getSort() : 0))
                    .collect(Collectors.toList());
        }

        List<Menu> parentMenus = menuMapper.selectBatchIds(parentIds);

        List<Menu> allMenus = new ArrayList<>(menus);
        allMenus.addAll(parentMenus);

        return fillParentMenus(allMenus);
    }

    @Override
    public List<Menu> selectMenusByRoleId(Long roleId) {
        // 使用缓存获取角色菜单
        return menuCacheService.getRoleMenusFromCache(roleId);
    }

    @Override
    public List<MenuVO> buildMenuTree(List<Menu> menus) {
        List<MenuVO> menuVOList = menus.stream().map(menu -> {
            MenuVO vo = new MenuVO();
            BeanUtils.copyProperties(menu, vo);
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        List<MenuVO> tree = new ArrayList<>(menuVOList.size() / 4);
        for (MenuVO menuVO : menuVOList) {
            if (menuVO.getParentId() == null || menuVO.getParentId() == 0) {
                tree.add(menuVO);
                buildChildren(menuVO, menuVOList);
            }
        }

        // 对根菜单按 sort 字段排序
        return tree.stream()
                .sorted(Comparator.comparingInt(menu -> menu.getSort() != null ? menu.getSort() : 0))
                .collect(Collectors.toList());
    }

    private void buildChildren(MenuVO parent, List<MenuVO> allMenus) {
        List<MenuVO> children = allMenus.stream()
                .filter(m -> parent.getId().equals(m.getParentId()))
                .sorted(Comparator.comparingInt(menu -> menu.getSort() != null ? menu.getSort() : 0))
                .collect(Collectors.toList());
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (MenuVO child : children) {
                buildChildren(child, allMenus);
            }
        }
    }

    @Override
    public List<MenuExcelDTO> exportMenus(List<Long> menuIds) {
        log.info("导出菜单数据开始, menuIds={}", menuIds);

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        if (menuIds != null && !menuIds.isEmpty()) {
            queryWrapper.in(Menu::getId, menuIds);
        }
        queryWrapper.orderByAsc(Menu::getSort);

        List<Menu> menuList = this.list(queryWrapper);
        List<MenuExcelDTO> excelDTOList = new ArrayList<>();

        for (Menu menu : menuList) {
            MenuExcelDTO dto = new MenuExcelDTO();
            BeanUtils.copyProperties(menu, dto);
            excelDTOList.add(dto);
        }

        log.info("导出菜单数据结束, count={}", excelDTOList.size());
        return excelDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importMenus(List<MenuExcelDTO> menuExcelDTOList) {
        log.info("导入菜单数据开始, count={}", menuExcelDTOList.size());

        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
        Map<String, Long> menuNameToIdMap = new HashMap<>(menuExcelDTOList.size());

        for (int i = 0; i < menuExcelDTOList.size(); i++) {
            MenuExcelDTO excelDTO = menuExcelDTOList.get(i);
            int rowNum = i + 2;

            try {
                if (validateMenuData(excelDTO, rowNum, failMessages)) {
                    continue;
                }

                if (checkMenuExists(excelDTO, rowNum, failMessages)) {
                    continue;
                }

                Long parentId = validateParentMenu(excelDTO, rowNum, failMessages);
                if (parentId == null) {
                    continue;
                }

                Menu menu = createMenuFromExcel(excelDTO, parentId);
                if (saveMenu(menu, excelDTO, menuNameToIdMap)) {
                    successCount++;
                } else {
                    failMessages.append(String.format("第%d行：保存菜单失败；", rowNum));
                    failCount++;
                }
            } catch (Exception e) {
                handleImportException(e, excelDTO, rowNum, failMessages);
                failCount++;
            }
        }

        log.info("导入菜单数据结束, success={}, fail={}", successCount, failCount);
        return buildImportResult(successCount, failCount, failMessages);
    }

    /**
     * 验证菜单数据
     */
    private boolean validateMenuData(MenuExcelDTO excelDTO, int rowNum, StringBuilder failMessages) {
        if (excelDTO.getName() == null || excelDTO.getName().trim().isEmpty()) {
            failMessages.append(String.format("第%d行：菜单名称不能为空；", rowNum));
            return true;
        }
        return false;
    }

    /**
     * 检查菜单是否已存在
     */
    private boolean checkMenuExists(MenuExcelDTO excelDTO, int rowNum, StringBuilder failMessages) {
        LambdaQueryWrapper<Menu> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(Menu::getName, excelDTO.getName());
        Menu existMenu = this.getOne(nameQuery);

        if (existMenu != null) {
            failMessages.append(String.format("第%d行：菜单名称%s已存在；", rowNum, excelDTO.getName()));
            return true;
        }
        return false;
    }

    /**
     * 验证父菜单
     */
    private Long validateParentMenu(MenuExcelDTO excelDTO, int rowNum, StringBuilder failMessages) {
        Long parentId = excelDTO.getParentId();
        if (parentId == null) {
            parentId = 0L;
        }

        if (parentId != 0) {
            Menu parentMenu = this.getById(parentId);
            if (parentMenu == null) {
                failMessages.append(String.format("第%d行：父菜单ID %d 不存在；", rowNum, parentId));
                return null;
            }
        }
        return parentId;
    }

    /**
     * 从Excel数据创建菜单对象
     */
    private Menu createMenuFromExcel(MenuExcelDTO excelDTO, Long parentId) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(excelDTO, menu);
        menu.setParentId(parentId);

        // 设置默认值
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getHidden() == null) {
            menu.setHidden(0);
        }
        if (menu.getExternal() == null) {
            menu.setExternal(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        return menu;
    }

    /**
     * 保存菜单
     */
    private boolean saveMenu(Menu menu, MenuExcelDTO excelDTO, Map<String, Long> menuNameToIdMap) {
        if (this.save(menu)) {
            menuNameToIdMap.put(excelDTO.getName(), menu.getId());
            log.info("成功导入菜单, name={}, id={}", menu.getName(), menu.getId());
            return true;
        }
        return false;
    }

    /**
     * 处理导入异常
     */
    private void handleImportException(Exception e, MenuExcelDTO excelDTO, int rowNum, StringBuilder failMessages) {
        log.error("导入菜单失败, row={}, name={}, error={}", rowNum, excelDTO.getName(), e.getMessage());
        failMessages.append(String.format("第%d行：%s；", rowNum, e.getMessage()));
    }

    /**
     * 构建导入结果
     */
    private String buildImportResult(int successCount, int failCount, StringBuilder failMessages) {
        String result = String.format("导入完成：成功%d条，失败%d条", successCount, failCount);
        if (failCount > 0) {
            result += "。失败详情：" + failMessages;
        }
        return result;
    }

    @Override
    public boolean deleteMenusByIds(Long[] menuIds) {
        log.info("批量删除菜单开始, menuIds={}, count={}", Arrays.toString(menuIds), menuIds.length);

        try {
            // 检查菜单是否有关联数据
            for (Long menuId : menuIds) {
                // 检查菜单是否有子菜单
                List<Menu> childMenus = menuMapper.selectList(new QueryWrapper<Menu>().eq("parent_id", menuId));
                if (!childMenus.isEmpty()) {
                    Menu menu = this.getById(menuId);
                    String menuName = menu != null ? menu.getName() : String.valueOf(menuId);
                    log.warn("批量删除菜单失败，菜单有子菜单, menuId={}, menuName={}", menuId, menuName);
                    throw new BizException("菜单 " + menuName + " 有子菜单，无法删除");
                }

                // 检查菜单是否分配给角色
                List<RoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().eq("menu_id", menuId));
                if (!roleMenus.isEmpty()) {
                    Menu menu = this.getById(menuId);
                    String menuName = menu != null ? menu.getName() : String.valueOf(menuId);
                    log.warn("批量删除菜单失败，菜单已分配给角色, menuId={}, menuName={}", menuId, menuName);
                    throw new BizException("菜单 " + menuName + " 已分配给角色，无法删除");
                }

                // 检查菜单是否有关联的权限
                List<Permission> permissions = permissionMapper.selectList(new QueryWrapper<Permission>().eq("menu_id", menuId));
                if (!permissions.isEmpty()) {
                    Menu menu = this.getById(menuId);
                    String menuName = menu != null ? menu.getName() : String.valueOf(menuId);
                    log.warn("批量删除菜单失败，菜单有关联的权限, menuId={}, menuName={}", menuId, menuName);
                    throw new BizException("菜单 " + menuName + " 有关联的权限，无法删除");
                }
            }

            // 批量删除菜单
            int result = menuMapper.deleteBatchIds(Arrays.asList(menuIds));

            if (result > 0) {
                // 清除所有菜单缓存
                menuCacheService.evictAllMenuCache();
            }

            log.info("批量删除菜单结束, count={}, result={}", menuIds.length, result);
            return result > 0;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除菜单失败", e);
            return false;
        }
    }

    /**
     * 导入结果内部类
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    private static class ImportResult {
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
    }
}
