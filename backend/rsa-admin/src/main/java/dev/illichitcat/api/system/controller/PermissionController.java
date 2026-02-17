package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.PermissionDTO;
import dev.illichitcat.system.model.dto.PermissionExcelDTO;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.service.PermissionService;
import dev.illichitcat.system.service.RolePermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "权限管理", description = "权限管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermService rolePermService;

    /**
     * 查询权限列表
     *
     * @param name   权限名称
     * @param perm   权限标识
     * @param menuId 菜单ID
     * @return 权限列表
     */
    @Operation(summary = "查询权限列表")
    @RequirePermission("permission:list")
    @GetMapping("/list")
    public Result<List<Permission>> list(
            @Parameter(description = "权限名称") @RequestParam(required = false) String name,
            @Parameter(description = "权限标识") @RequestParam(required = false) String perm,
            @Parameter(description = "菜单ID") @RequestParam(required = false) Long menuId) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setPerm(perm);
        permission.setMenuId(menuId);
        List<Permission> list = permissionService.selectPermissionListWithMenuName(permission);
        return Result.ok(list);
    }

    /**
     * 查询权限树
     *
     * @return 权限树
     */
    @Operation(summary = "查询权限树")
    @RequirePermission("permission:list")
    @GetMapping("/tree")
    public Result<List<Permission>> tree() {
        List<Permission> list = permissionService.selectPermissionList(new Permission());
        return Result.ok(list);
    }

    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    @Operation(summary = "根据ID查询权限")
    @RequirePermission("permission:list")
    @GetMapping("/{id}")
    public Result<Permission> getById(@Parameter(description = "权限ID") @PathVariable Long id) {
        Permission permission = permissionService.selectPermissionById(id);
        if (permission == null) {
            return Result.fail(404, "权限不存在");
        }
        return Result.ok(permission);
    }

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Operation(summary = "根据用户ID查询权限列表")
    @RequirePermission("permission:list")
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Permission> list = permissionService.selectPermissionsByUserId(userId);
        return Result.ok(list);
    }

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Operation(summary = "根据角色ID查询权限列表")
    @RequirePermission("permission:list")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getByRoleId(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Permission> list = permissionService.selectPermissionsByRoleId(roleId);
        return Result.ok(list);
    }

    /**
     * 新增权限
     *
     * @param dto 权限信息
     * @return 操作结果
     */
    @Operation(summary = "新增权限")
    @RequirePermission("permission:edit")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@RequestBody PermissionDTO dto) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        boolean success = permissionService.insertPermission(permission);
        return success ? Result.ok() : Result.fail("新增权限失败");
    }

    /**
     * 更新权限
     *
     * @param dto 权限信息
     * @return 操作结果
     */
    @Operation(summary = "更新权限")
    @RequirePermission("permission:edit")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@RequestBody PermissionDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(400, "权限ID不能为空");
        }
        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        boolean success = permissionService.updatePermission(permission);
        return success ? Result.ok() : Result.fail("更新权限失败");
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 操作结果
     */
    @Operation(summary = "删除权限")
    @RequirePermission("permission:edit")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "权限ID") @PathVariable Long id) {
        // 删除角色权限关联
        rolePermService.deleteByPermId(id);
        // 删除权限
        boolean success = permissionService.deletePermissionById(id);
        return success ? Result.ok() : Result.fail("删除权限失败");
    }

    /**
     * 导出权限数据
     *
     * @param ids 权限ID数组，为空则导出所有
     */
    @Operation(summary = "导出权限数据")
    @RequirePermission("permission:export")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody(required = false) Long[] ids, HttpServletResponse response) throws IOException {
        List<Long> permIds = ids != null ? Arrays.asList(ids) : null;
        List<PermissionExcelDTO> permissionList = permissionService.exportPermissions(permIds);
        ExcelUtils.exportExcel(response, permissionList, PermissionExcelDTO.class, "权限数据", "权限列表");
    }

    /**
     * 导入权限数据
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @Operation(summary = "导入权限数据")
    @RequirePermission("permission:import")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<PermissionExcelDTO> permissionList = ExcelUtils.importExcel(file, PermissionExcelDTO.class);
            String result = permissionService.importPermissions(permissionList);
            return Result.ok(result);
        } catch (IOException e) {
            log.error("导入权限数据失败", e);
            return Result.fail("文件解析失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("导入权限数据失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载权限导入模板
     */
    @Operation(summary = "下载权限导入模板")
    @RequirePermission("permission:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个示例数据作为模板
        List<PermissionExcelDTO> templateData = new ArrayList<>();
        PermissionExcelDTO template = new PermissionExcelDTO();
        template.setPerm("user:list");
        template.setName("查询用户");
        template.setMenuId(3L);
        template.setMenuName("用户管理");
        templateData.add(template);

        ExcelUtils.exportExcel(response, templateData, PermissionExcelDTO.class, "权限导入模板", "模板说明");
    }

    /**
     * 批量删除权限
     *
     * @param ids 权限ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除权限")
    @RequirePermission("permission:delete")
    @OperationLog(title = "权限管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = permissionService.deletePermissionsByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除权限失败");
    }
}