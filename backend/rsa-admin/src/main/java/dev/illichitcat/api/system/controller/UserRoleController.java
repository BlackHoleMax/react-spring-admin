package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.UserRoleDTO;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.service.RoleService;
import dev.illichitcat.system.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "用户角色管理", description = "用户角色关联相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Operation(summary = "获取用户角色列表")
    @RequirePermission("user:list")
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Role> roles = roleService.selectRolesByUserId(userId);
        return Result.ok(roles);
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 用户角色ID列表
     */
    @Operation(summary = "获取用户角色ID列表")
    @RequirePermission("user:list")
    @GetMapping("/user/{userId}/ids")
    public Result<List<Long>> getUserRoleIds(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Long> roleIds = userRoleService.selectRoleIdsByUserId(userId);
        return Result.ok(roleIds);
    }

    /**
     * 保存用户角色关联
     *
     * @param dto 用户角色关联信息
     * @return 操作结果
     */
    @Operation(summary = "保存用户角色关联")
    @RequirePermission("user:edit")
    @OperationLog(title = "用户角色管理", businessType = OperationLog.BusinessType.GRANT)
    @PostMapping
    public Result<Void> saveUserRoles(@RequestBody UserRoleDTO dto) {
        boolean success = userRoleService.saveUserRoles(dto.getUserId(), dto.getRoleIds());
        return success ? Result.ok() : Result.fail("保存用户角色失败");
    }
}