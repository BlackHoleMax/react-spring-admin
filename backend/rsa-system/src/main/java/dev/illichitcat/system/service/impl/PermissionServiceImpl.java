package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.system.dao.mapper.MenuMapper;
import dev.illichitcat.system.dao.mapper.PermissionMapper;
import dev.illichitcat.system.dao.mapper.RolePermMapper;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.model.dto.PermissionExcelDTO;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.model.entity.RolePerm;
import dev.illichitcat.system.service.MenuService;
import dev.illichitcat.system.service.PermissionCacheService;
import dev.illichitcat.system.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RolePermMapper rolePermMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    @Lazy
    private PermissionCacheService permissionCacheService;

    @Override
    public List<Permission> selectPermissionList(Permission permission) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        if (permission != null) {
            if (permission.getName() != null && !permission.getName().trim().isEmpty()) {
                queryWrapper.like(Permission::getName, permission.getName());
            }
            if (permission.getPerm() != null && !permission.getPerm().trim().isEmpty()) {
                queryWrapper.like(Permission::getPerm, permission.getPerm());
            }
            if (permission.getMenuId() != null) {
                queryWrapper.eq(Permission::getMenuId, permission.getMenuId());
            }
        }
        queryWrapper.orderByAsc(Permission::getMenuId, Permission::getId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Permission> selectPermissionListWithMenuName(Permission permission) {
        List<Permission> list = selectPermissionList(permission);
        if (list.isEmpty()) {
            return list;
        }

        // 获取所有菜单ID
        List<Long> menuIds = list.stream()
                .map(Permission::getMenuId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return list;
        }

        // 查询菜单信息
        List<Menu> menus = menuMapper.selectBatchIds(menuIds);
        Map<Long, String> menuNameMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Menu::getName, (v1, v2) -> v1));

        // 填充菜单名称
        list.forEach(perm -> {
            if (perm.getMenuId() != null) {
                perm.setMenuName(menuNameMap.get(perm.getMenuId()));
            }
        });

        return list;
    }

    @Override
    public Permission selectPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public boolean insertPermission(Permission permission) {
        log.info("新增权限: {}", permission.getName());
        boolean result = permissionMapper.insert(permission) > 0;
        if (result) {
            // 清除所有权限缓存
            permissionCacheService.evictAllPermissionCache();
        }
        return result;
    }

    @Override
    public boolean updatePermission(Permission permission) {
        log.info("更新权限: id={}", permission.getId());
        boolean result = permissionMapper.updateById(permission) > 0;
        if (result) {
            // 清除所有权限缓存
            permissionCacheService.evictAllPermissionCache();
        }
        return result;
    }

    @Override
    public boolean deletePermissionById(Long id) {
        log.info("删除权限: id={}", id);

        // 检查权限是否分配给角色
        List<RolePerm> rolePerms = rolePermMapper.selectList(new QueryWrapper<RolePerm>().eq("perm_id", id));
        if (!rolePerms.isEmpty()) {
            Permission permission = this.getById(id);
            String permName = permission != null ? permission.getName() : String.valueOf(id);
            log.warn("删除权限失败，权限已分配给角色, permId={}, permName={}", id, permName);
            throw new BizException("权限 " + permName + " 已分配给角色，无法删除");
        }

        boolean result = permissionMapper.deleteById(id) > 0;
        if (result) {
            // 清除所有权限缓存
            permissionCacheService.evictAllPermissionCache();
        }
        return result;
    }

    @Override
    public List<Permission> selectPermissionsByUserId(Long userId) {
        return permissionCacheService.getUserPermissionsFromCache(userId);
    }

    @Override
    public List<String> selectPermsByUserId(Long userId) {
        return permissionCacheService.getUserPermsFromCache(userId);
    }

    @Override
    public List<Permission> selectPermissionsByRoleId(Long roleId) {
        return permissionCacheService.getRolePermissionsFromCache(roleId);
    }

    @Override
    public List<PermissionExcelDTO> exportPermissions(List<Long> permIds) {
        log.info("导出权限数据开始, permIds={}", permIds);

        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        if (permIds != null && !permIds.isEmpty()) {
            queryWrapper.in(Permission::getId, permIds);
        }
        queryWrapper.orderByAsc(Permission::getId);

        List<Permission> permissionList = this.list(queryWrapper);
        List<PermissionExcelDTO> excelDTOList = new ArrayList<>();

        for (Permission permission : permissionList) {
            PermissionExcelDTO dto = new PermissionExcelDTO();
            BeanUtils.copyProperties(permission, dto);

            // 获取关联菜单名称
            if (permission.getMenuId() != null) {
                Menu menu = menuService.getById(permission.getMenuId());
                if (menu != null) {
                    dto.setMenuName(menu.getName());
                }
            }

            excelDTOList.add(dto);
        }

        log.info("导出权限数据结束, count={}", excelDTOList.size());
        return excelDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importPermissions(List<PermissionExcelDTO> permissionExcelDTOList) {
        log.info("导入权限数据开始, count={}", permissionExcelDTOList.size());

        ImportResult result = new ImportResult();

        for (int i = 0; i < permissionExcelDTOList.size(); i++) {
            PermissionExcelDTO excelDTO = permissionExcelDTOList.get(i);
            int rowNum = i + 2;

            try {
                processPermissionImport(excelDTO, rowNum, result);
            } catch (Exception e) {
                handleImportException(excelDTO, rowNum, e, result);
            }
        }

        log.info("导入权限数据结束, success={}, fail={}", result.successCount, result.failCount);
        return buildImportResult(result);
    }

    /**
     * 处理单个权限导入
     */
    private void processPermissionImport(PermissionExcelDTO excelDTO, int rowNum, ImportResult result) {
        // 验证必填字段
        if (!validatePermissionFields(excelDTO, rowNum, result)) {
            return;
        }

        // 检查权限标识是否已存在
        if (!checkPermissionExists(excelDTO, rowNum, result)) {
            return;
        }

        // 处理菜单关联
        Long menuId = resolveMenuAssociation(excelDTO, rowNum, result);
        boolean hasMenuAssociation = excelDTO.getMenuId() != null ||
                (excelDTO.getMenuName() != null && !excelDTO.getMenuName().trim().isEmpty());
        if (menuId == null && hasMenuAssociation) {
            return;
        }

        // 创建并保存权限
        Permission permission = createPermissionFromExcelDTO(excelDTO, menuId);
        if (this.save(permission)) {
            result.successCount++;
            log.info("成功导入权限, perm={}, id={}", permission.getPerm(), permission.getId());
        } else {
            result.failMessages.append(String.format("第%d行：保存权限失败；", rowNum));
            result.failCount++;
        }
    }

    /**
     * 验证权限必填字段
     */
    private boolean validatePermissionFields(PermissionExcelDTO excelDTO, int rowNum, ImportResult result) {
        if (excelDTO.getPerm() == null || excelDTO.getPerm().trim().isEmpty()) {
            result.failMessages.append(String.format("第%d行：权限标识不能为空；", rowNum));
            result.failCount++;
            return false;
        }

        if (excelDTO.getName() == null || excelDTO.getName().trim().isEmpty()) {
            result.failMessages.append(String.format("第%d行：权限名称不能为空；", rowNum));
            result.failCount++;
            return false;
        }

        return true;
    }

    /**
     * 检查权限标识是否已存在
     */
    private boolean checkPermissionExists(PermissionExcelDTO excelDTO, int rowNum, ImportResult result) {
        LambdaQueryWrapper<Permission> permQuery = new LambdaQueryWrapper<>();
        permQuery.eq(Permission::getPerm, excelDTO.getPerm());
        Permission existPermission = this.getOne(permQuery);
        if (existPermission != null) {
            result.failMessages.append(String.format("第%d行：权限标识%s已存在；", rowNum, excelDTO.getPerm()));
            result.failCount++;
            return false;
        }
        return true;
    }

    /**
     * 解析菜单关联
     */
    private Long resolveMenuAssociation(PermissionExcelDTO excelDTO, int rowNum, ImportResult result) {
        if (excelDTO.getMenuId() != null) {
            return validateMenuById(excelDTO.getMenuId(), rowNum, result);
        } else if (excelDTO.getMenuName() != null && !excelDTO.getMenuName().trim().isEmpty()) {
            return validateMenuByName(excelDTO.getMenuName(), rowNum, result);
        }
        return null;
    }

    /**
     * 通过ID验证菜单
     */
    private Long validateMenuById(Long menuId, int rowNum, ImportResult result) {
        Menu menu = menuService.getById(menuId);
        if (menu == null) {
            result.failMessages.append(String.format("第%d行：菜单ID %d 不存在；", rowNum, menuId));
            result.failCount++;
            return null;
        }
        return menuId;
    }

    /**
     * 通过名称验证菜单
     */
    private Long validateMenuByName(String menuName, int rowNum, ImportResult result) {
        LambdaQueryWrapper<Menu> menuQuery = new LambdaQueryWrapper<>();
        menuQuery.eq(Menu::getName, menuName);
        Menu menu = menuService.getOne(menuQuery);
        if (menu != null) {
            return menu.getId();
        } else {
            result.failMessages.append(String.format("第%d行：菜单名称%s不存在；", rowNum, menuName));
            result.failCount++;
            return null;
        }
    }

    /**
     * 从ExcelDTO创建权限对象
     */
    private Permission createPermissionFromExcelDTO(PermissionExcelDTO excelDTO, Long menuId) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(excelDTO, permission);
        permission.setMenuId(menuId);
        return permission;
    }

    /**
     * 处理导入异常
     */
    private void handleImportException(PermissionExcelDTO excelDTO, int rowNum, Exception e, ImportResult result) {
        log.error("导入权限失败, row={}, perm={}, error={}", rowNum, excelDTO.getPerm(), e.getMessage());
        result.failMessages.append(String.format("第%d行：%s；", rowNum, e.getMessage()));
        result.failCount++;
    }

    /**
     * 构建导入结果字符串
     */
    private String buildImportResult(ImportResult result) {
        String resultStr = String.format("导入完成：成功%d条，失败%d条", result.successCount, result.failCount);
        if (result.failCount > 0) {
            resultStr += "。失败详情：" + result.failMessages;
        }
        return resultStr;
    }

    @Override
    public boolean deletePermissionsByIds(Long[] permIds) {
        log.info("批量删除权限开始, permIds={}, count={}", Arrays.toString(permIds), permIds.length);

        try {
            // 检查权限是否有关联数据
            for (Long permId : permIds) {
                // 检查权限是否分配给角色
                List<RolePerm> rolePerms = rolePermMapper.selectList(new QueryWrapper<RolePerm>().eq("perm_id", permId));
                if (!rolePerms.isEmpty()) {
                    Permission permission = this.getById(permId);
                    String permName = permission != null ? permission.getName() : String.valueOf(permId);
                    log.warn("批量删除权限失败，权限已分配给角色, permId={}, permName={}", permId, permName);
                    throw new BizException("权限 " + permName + " 已分配给角色，无法删除");
                }
            }

            // 批量删除权限
            int result = permissionMapper.deleteBatchIds(Arrays.asList(permIds));

            if (result > 0) {
                // 清除所有权限缓存
                permissionCacheService.evictAllPermissionCache();
            }

            log.info("批量删除权限结束, count={}, result={}", permIds.length, result);
            return result > 0;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除权限失败", e);
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
