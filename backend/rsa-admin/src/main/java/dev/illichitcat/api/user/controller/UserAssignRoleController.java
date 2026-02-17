package dev.illichitcat.api.user.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.RoleDTO;
import dev.illichitcat.system.model.dto.UserRoleDTO;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.service.RoleService;
import dev.illichitcat.system.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户分配角色管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "用户分配角色管理", description = "用户分配角色相关的接口")
@RestController
@RequestMapping("/api/user/assign-role")
public class UserAssignRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @Operation(summary = "根据ID获取角色", description = "根据角色ID获取角色详细信息")
    @Parameters({
            @Parameter(name = "id", description = "角色ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功获取角色信息", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @RequirePermission("role:list")
    @GetMapping("/{id}")
    public Result<RoleDTO> getRoleById(@PathVariable Long id) {
        Role role = roleService.selectRoleById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }

        RoleDTO roleDTO = convertToDTO(role);
        return Result.ok(roleDTO);
    }

    /**
     * 获取用户的角色
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Operation(summary = "获取用户的角色", description = "根据用户ID获取该用户拥有的角色列表")
    @Parameters({
            @Parameter(name = "userId", description = "用户ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功获取用户角色列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @RequirePermission("user:list")
    @GetMapping("/user/{userId}")
    public Result<List<RoleDTO>> getRolesByUserId(@PathVariable Long userId) {
        List<Role> roles = roleService.selectRolesByUserId(userId);
        List<RoleDTO> roleDtos = roles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(roleDtos);
    }

    /**
     * 为用户分配角色
     *
     * @param userRoleDTO 用户角色分配信息
     * @return 操作结果
     */
    @Operation(summary = "为用户分配角色", description = "为指定用户分配角色")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户角色分配信息", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRoleDTO.class)), required = true)
    @ApiResponse(responseCode = "200", description = "成功分配角色", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @RequirePermission("user:edit")
    @OperationLog(title = "用户分配角色", businessType = OperationLog.BusinessType.GRANT)
    @PostMapping("/assign")
    public Result<Void> assignRolesToUser(@RequestBody UserRoleDTO userRoleDTO) {
        if (userRoleDTO == null || userRoleDTO.getUserId() == null || userRoleDTO.getRoleIds() == null) {
            return Result.fail("参数不能为空");
        }

        boolean result = userRoleService.assignRolesToUser(userRoleDTO.getUserId(), userRoleDTO.getRoleIds());
        if (!result) {
            return Result.fail("分配角色失败");
        }

        return Result.ok();
    }

    /**
     * 将Role实体转换为RoleDTO
     *
     * @param role Role实体
     * @return RoleDTO
     */
    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        if (role != null) {
            BeanUtils.copyProperties(role, dto);
        }
        return dto;
    }
}