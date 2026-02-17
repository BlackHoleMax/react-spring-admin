package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.system.dao.mapper.RoleMapper;
import dev.illichitcat.system.dao.mapper.RoleMenuMapper;
import dev.illichitcat.system.dao.mapper.RolePermMapper;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.manager.RoleManager;
import dev.illichitcat.system.model.dto.RoleExcelDTO;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.model.entity.UserRole;
import dev.illichitcat.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private RoleCacheService roleCacheService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RolePermService rolePermService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermMapper rolePermMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public IPage<Role> selectRoleList(Page<Role> page, Role role) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        if (role.getName() != null && !role.getName().isEmpty()) {
            queryWrapper.like(Role::getName, role.getName());
        }
        if (role.getCode() != null && !role.getCode().isEmpty()) {
            queryWrapper.like(Role::getCode, role.getCode());
        }
        if (role.getStatus() != null) {
            queryWrapper.eq(Role::getStatus, role.getStatus());
        }
        queryWrapper.orderByAsc(Role::getSort);
        return roleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Role selectRoleById(Long id) {
        return roleCacheService.getRoleFromCache(id);
    }

    @Override
    public boolean insertRole(Role role) {
        log.info("新增角色: {}", role.getName());
        boolean result = roleMapper.insert(role) > 0;
        if (result) {
            // 新增角色后，缓存该角色
            roleCacheService.cacheRole(role);
        }
        return result;
    }

    @Override
    public boolean updateRole(Role role) {
        log.info("更新角色: id={}", role.getId());
        boolean result = roleMapper.updateById(role) > 0;
        if (result) {
            // 更新角色后，清除该角色缓存
            roleCacheService.evictRoleCache(role.getId());
            if (role.getCode() != null) {
                roleCacheService.evictRoleCache(role.getCode());
            }
        }
        return result;
    }

    @Override
    public boolean deleteRoleById(Long id) {
        log.info("删除角色: id={}", id);

        // 检查角色是否分配给用户
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("role_id", id));
        if (!userRoles.isEmpty()) {
            Role role = this.getById(id);
            String roleName = role != null ? role.getName() : String.valueOf(id);
            log.warn("删除角色失败，角色已分配给用户, roleId={}, roleName={}", id, roleName);
            throw new BizException("角色 " + roleName + " 已分配给用户，无法删除");
        }

        boolean result = roleMapper.deleteById(id) > 0;
        if (result) {
            // 删除角色后，清除该角色缓存
            roleCacheService.evictRoleCache(id);
        }
        return result;
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        // 通过Manager层获取角色列表，实现缓存等功能
        return roleManager.selectRolesByUserId(userId);
    }

    @Override
    public List<RoleExcelDTO> exportRoles(List<Long> roleIds) {
        log.info("导出角色数据开始, roleIds={}", roleIds);

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            queryWrapper.in(Role::getId, roleIds);
        }
        queryWrapper.orderByAsc(Role::getSort);

        List<Role> roleList = this.list(queryWrapper);
        List<RoleExcelDTO> excelDTOList = new ArrayList<>();

        for (Role role : roleList) {
            RoleExcelDTO dto = new RoleExcelDTO();
            BeanUtils.copyProperties(role, dto);

            // 获取角色权限标识
            List<Long> permIds = rolePermService.selectPermIdsByRoleId(role.getId());
            List<Permission> permissions = permissionService.listByIds(permIds);
            String permStr = permissions.stream()
                    .map(Permission::getPerm)
                    .collect(Collectors.joining(","));
            dto.setPermissions(permStr);

            // 获取角色菜单ID
            List<Long> menuIds = roleMenuService.selectMenuIdsByRoleId(role.getId());
            String menuIdStr = menuIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            dto.setMenuIds(menuIdStr);

            excelDTOList.add(dto);
        }

        log.info("导出角色数据结束, count={}", excelDTOList.size());
        return excelDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importRoles(List<RoleExcelDTO> roleExcelDTOList) {
        log.info("导入角色数据开始, count={}", roleExcelDTOList.size());

        ImportResult result = new ImportResult();

        for (int i = 0; i < roleExcelDTOList.size(); i++) {
            RoleExcelDTO excelDTO = roleExcelDTOList.get(i);
            int rowNum = i + 2;

            try {
                processRoleImport(excelDTO, rowNum, result);
            } catch (Exception e) {
                handleImportException(excelDTO, rowNum, e, result);
            }
        }

        log.info("导入角色数据结束, success={}, fail={}", result.successCount, result.failCount);
        return buildImportResult(result);
    }

    /**
     * 处理单个角色导入
     */
    private void processRoleImport(RoleExcelDTO excelDTO, int rowNum, ImportResult result) {
        // 验证必填字段
        if (!validateRoleFields(excelDTO, rowNum, result)) {
            return;
        }

        // 检查角色编码是否已存在
        if (!checkRoleCodeExists(excelDTO, rowNum, result)) {
            return;
        }

        // 创建并保存角色
        Role role = createRoleFromExcelDTO(excelDTO);
        if (this.save(role)) {
            // 处理权限和菜单关联
            processRoleAssociations(role, excelDTO);
            result.successCount++;
            log.info("成功导入角色, name={}, id={}", role.getName(), role.getId());
        } else {
            result.failMessages.append(String.format("第%d行：保存角色失败；", rowNum));
            result.failCount++;
        }
    }

    /**
     * 验证角色必填字段
     */
    private boolean validateRoleFields(RoleExcelDTO excelDTO, int rowNum, ImportResult result) {
        if (excelDTO.getName() == null || excelDTO.getName().trim().isEmpty()) {
            result.failMessages.append(String.format("第%d行：角色名称不能为空；", rowNum));
            result.failCount++;
            return false;
        }

        if (excelDTO.getCode() == null || excelDTO.getCode().trim().isEmpty()) {
            result.failMessages.append(String.format("第%d行：角色编码不能为空；", rowNum));
            result.failCount++;
            return false;
        }

        return true;
    }

    /**
     * 检查角色编码是否已存在
     */
    private boolean checkRoleCodeExists(RoleExcelDTO excelDTO, int rowNum, ImportResult result) {
        LambdaQueryWrapper<Role> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(Role::getCode, excelDTO.getCode());
        Role existRole = this.getOne(codeQuery);
        if (existRole != null) {
            result.failMessages.append(String.format("第%d行：角色编码%s已存在；", rowNum, excelDTO.getCode()));
            result.failCount++;
            return false;
        }
        return true;
    }

    /**
     * 从ExcelDTO创建角色对象
     */
    private Role createRoleFromExcelDTO(RoleExcelDTO excelDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(excelDTO, role);

        // 设置默认状态和排序
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }

        return role;
    }

    /**
     * 处理角色关联（权限和菜单）
     */
    private void processRoleAssociations(Role role, RoleExcelDTO excelDTO) {
        // 处理权限关联
        processPermissionAssociations(role.getId(), excelDTO.getPermissions());

        // 处理菜单关联
        processMenuAssociations(role.getId(), excelDTO.getMenuIds());
    }

    /**
     * 处理权限关联
     */
    private void processPermissionAssociations(Long roleId, String permissions) {
        if (permissions != null && !permissions.trim().isEmpty()) {
            List<Long> permIds = resolvePermissionIds(permissions);
            if (!permIds.isEmpty()) {
                rolePermService.saveRolePerms(roleId, permIds);
            }
        }
    }

    /**
     * 解析权限ID列表
     */
    private List<Long> resolvePermissionIds(String permissions) {
        String[] permArray = permissions.split(",");
        List<Long> permIds = new ArrayList<>(permArray.length);

        for (String perm : permArray) {
            perm = perm.trim();
            LambdaQueryWrapper<Permission> permQuery = new LambdaQueryWrapper<>();
            permQuery.eq(Permission::getPerm, perm);
            Permission permission = permissionService.getOne(permQuery);
            if (permission != null) {
                permIds.add(permission.getId());
            }
        }

        return permIds;
    }

    /**
     * 处理菜单关联
     */
    private void processMenuAssociations(Long roleId, String menuIds) {
        if (menuIds != null && !menuIds.trim().isEmpty()) {
            List<Long> ids = resolveMenuIds(menuIds);
            if (!ids.isEmpty()) {
                roleMenuService.saveRoleMenus(roleId, ids);
            }
        }
    }

    /**
     * 解析菜单ID列表
     */
    private List<Long> resolveMenuIds(String menuIds) {
        String[] menuIdArray = menuIds.split(",");
        List<Long> ids = new ArrayList<>(menuIdArray.length);

        for (String menuIdStr : menuIdArray) {
            try {
                Long menuId = Long.valueOf(menuIdStr.trim());
                // 验证菜单是否存在
                Menu menu = menuService.getById(menuId);
                if (menu != null) {
                    ids.add(menuId);
                }
            } catch (NumberFormatException e) {
                log.warn("无效的菜单ID: {}", menuIdStr);
            }
        }

        return ids;
    }

    /**
     * 处理导入异常
     */
    private void handleImportException(RoleExcelDTO excelDTO, int rowNum, Exception e, ImportResult result) {
        log.error("导入角色失败, row={}, name={}, error={}", rowNum, excelDTO.getName(), e.getMessage());
        result.failMessages.append(String.format("第%d行：%s；", rowNum, e.getMessage()));
        result.failCount++;
    }

    /**
     * 构建导入结果字符串
     */
    private String buildImportResult(ImportResult result) {
        String resultMessage = String.format("导入完成：成功%d条，失败%d条", result.successCount, result.failCount);
        if (!result.failMessages.toString().isEmpty()) {
            resultMessage += "\\n失败详情：" + result.failMessages;
        }
        return resultMessage;
    }

    @Override
    public boolean deleteRolesByIds(Long[] roleIds) {
        log.info("批量删除角色开始, roleIds={}, count={}", Arrays.toString(roleIds), roleIds.length);

        try {
            // 检查角色是否有关联数据
            for (Long roleId : roleIds) {
                // 检查角色是否分配给用户
                List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("role_id", roleId));
                if (!userRoles.isEmpty()) {
                    Role role = this.getById(roleId);
                    String roleName = role != null ? role.getName() : String.valueOf(roleId);
                    log.warn("批量删除角色失败，角色已分配给用户, roleId={}, roleName={}", roleId, roleName);
                    throw new BizException("角色 " + roleName + " 已分配给用户，无法删除");
                }
            }

            // 删除角色关联的权限
            for (Long roleId : roleIds) {
                rolePermMapper.deleteByRoleId(roleId);
            }

            // 删除角色关联的菜单
            for (Long roleId : roleIds) {
                roleMenuMapper.deleteByRoleId(roleId);
            }

            // 批量删除角色
            int result = roleMapper.deleteBatchIds(Arrays.asList(roleIds));

            if (result > 0) {
                // 批量删除角色后，清除缓存
                for (Long roleId : roleIds) {
                    roleCacheService.evictRoleCache(roleId);
                }
            }

            log.info("批量删除角色结束, count={}, result={}", roleIds.length, result);
            return result > 0;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除角色失败", e);
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