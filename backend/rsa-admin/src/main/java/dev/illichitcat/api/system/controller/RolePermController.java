package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.service.PermissionService;
import dev.illichitcat.system.service.RolePermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "角色权限管理", description = "角色权限管理相关接口")
@RestController
@RequestMapping("/api/system/role-perm")
public class RolePermController {

    @Autowired
    private RolePermService rolePermService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Operation(summary = "根据角色ID查询权限ID列表")
    @GetMapping("/list/{roleId}")
    public Result<List<Long>> listPermIdsByRoleId(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Long> permIds = rolePermService.selectPermIdsByRoleId(roleId);
        return Result.ok(permIds);
    }

    /**
     * 保存角色权限关联
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @return 操作结果
     */
    @Operation(summary = "保存角色权限关联")
    @RequirePermission("role:edit")
    @OperationLog(title = "角色权限管理", businessType = OperationLog.BusinessType.GRANT)
    @PostMapping("/{roleId}")
    public Result<Void> saveRolePerms(@Parameter(description = "角色ID") @PathVariable Long roleId,
                                      @Parameter(description = "权限ID列表") @RequestBody List<Long> permIds) {
        boolean success = rolePermService.saveRolePerms(roleId, permIds);
        return success ? Result.ok() : Result.fail("保存角色权限关联失败");
    }

    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    @Operation(summary = "获取所有权限列表")
    @GetMapping("/permissions")
    public Result<List<Permission>> listPermissions() {
        List<Permission> permissions = permissionService.list();
        return Result.ok(permissions);
    }
}