package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.RoleDTO;
import dev.illichitcat.system.model.dto.RoleExcelDTO;
import dev.illichitcat.system.model.dto.RoleMenuDTO;
import dev.illichitcat.system.model.dto.RolePermDTO;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.model.query.RoleQuery;
import dev.illichitcat.system.service.RoleMenuService;
import dev.illichitcat.system.service.RolePermService;
import dev.illichitcat.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
 * 角色管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RolePermService rolePermService;

    /**
     * 分页查询角色列表
     *
     * @param query 角色查询条件
     * @return 角色分页列表
     */
    @Operation(summary = "分页查询角色列表")
    @RequirePermission("role:list")
    @GetMapping("/list")
    public Result<IPage<Role>> list(RoleQuery query) {
        Role role = new Role();
        role.setName(query.getName());
        role.setCode(query.getCode());
        role.setStatus(query.getStatus());
        Page<Role> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Role> rolePage = roleService.selectRoleList(page, role);
        return Result.ok(rolePage, toPageInfo(rolePage));
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Operation(summary = "查询所有角色")
    @RequirePermission("role:list")
    @GetMapping("/all")
    public Result<List<Role>> all() {
        return Result.ok(roleService.list());
    }

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @Operation(summary = "根据ID查询角色")
    @RequirePermission("role:list")
    @GetMapping("/{id}")
    public Result<Role> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        Role role = roleService.selectRoleById(id);
        if (role == null) {
            return Result.fail(ExceptionCodes.NOT_FOUND, "角色不存在");
        }
        return Result.ok(role);
    }

    /**
     * 新增角色
     *
     * @param dto 角色信息
     * @return 操作结果
     */
    @Operation(summary = "新增角色")
    @RequirePermission("role:add")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody RoleDTO dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        boolean success = roleService.insertRole(role);
        return success ? Result.ok() : Result.fail("新增角色失败");
    }

    /**
     * 更新角色
     *
     * @param dto 角色信息
     * @return 操作结果
     */
    @Operation(summary = "更新角色")
    @RequirePermission("role:edit")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(ExceptionCodes.PARAM_ERROR, "角色ID不能为空");
        }
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        boolean success = roleService.updateRole(role);
        return success ? Result.ok() : Result.fail("更新角色失败");
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @Operation(summary = "删除角色")
    @RequirePermission("role:delete")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        // 删除角色菜单关联
        roleMenuService.deleteByRoleId(id);
        // 删除角色权限关联
        rolePermService.deleteByRoleId(id);
        // 删除角色
        boolean success = roleService.deleteRoleById(id);
        return success ? Result.ok() : Result.fail("删除角色失败");
    }

    /**
     * 获取角色菜单ID列表
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @Operation(summary = "获取角色菜单ID列表")
    @RequirePermission("role:list")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<Long> menuIds = roleMenuService.selectMenuIdsByRoleId(id);
        return Result.ok(menuIds);
    }

    /**
     * 保存角色菜单关联
     *
     * @param dto 角色菜单关联信息
     * @return 操作结果
     */
    @Operation(summary = "保存角色菜单关联")
    @RequirePermission("role:edit")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.GRANT)
    @PostMapping("/menus")
    public Result<Void> saveRoleMenus(@RequestBody RoleMenuDTO dto) {
        boolean success = roleMenuService.saveRoleMenus(dto.getRoleId(), dto.getMenuIds());
        return success ? Result.ok() : Result.fail("保存角色菜单失败");
    }

    /**
     * 获取角色权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @Operation(summary = "获取角色权限ID列表")
    @RequirePermission("role:list")
    @GetMapping("/{id}/perms")
    public Result<List<Long>> getRolePerms(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<Long> permIds = rolePermService.selectPermIdsByRoleId(id);
        return Result.ok(permIds);
    }

    /**
     * 保存角色权限关联
     *
     * @param dto 角色权限关联信息
     * @return 操作结果
     */
    @Operation(summary = "保存角色权限关联")
    @RequirePermission("role:edit")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.GRANT)
    @PostMapping("/perms")
    public Result<Void> saveRolePerms(@RequestBody RolePermDTO dto) {
        boolean success = rolePermService.saveRolePerms(dto.getRoleId(), dto.getPermIds());
        return success ? Result.ok() : Result.fail("保存角色权限失败");
    }

    private Result.PageInfo toPageInfo(IPage<?> page) {
        return new Result.PageInfo()
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }

    /**
     * 导出角色数据
     *
     * @param ids 角色ID数组，为空则导出所有
     */
    @Operation(summary = "导出角色数据")
    @RequirePermission("role:export")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody(required = false) Long[] ids, HttpServletResponse response) throws IOException {
        List<Long> roleIds = ids != null ? Arrays.asList(ids) : null;
        List<RoleExcelDTO> roleList = roleService.exportRoles(roleIds);
        ExcelUtils.exportExcel(response, roleList, RoleExcelDTO.class, "角色数据", "角色列表");
    }

    /**
     * 导入角色数据
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @Operation(summary = "导入角色数据")
    @RequirePermission("role:import")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<RoleExcelDTO> roleList = ExcelUtils.importExcel(file, RoleExcelDTO.class);
            String result = roleService.importRoles(roleList);
            return Result.ok(result);
        } catch (IOException e) {
            log.error("导入角色数据失败", e);
            return Result.fail("文件解析失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("导入角色数据失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载角色导入模板
     */
    @Operation(summary = "下载角色导入模板")
    @RequirePermission("role:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个示例数据作为模板
        List<RoleExcelDTO> templateData = new ArrayList<>();
        RoleExcelDTO template = new RoleExcelDTO();
        template.setName("普通管理员");
        template.setCode("admin");
        template.setSort(1);
        template.setStatus(1);
        template.setPermissions("user:list,user:add,user:edit");
        template.setMenuIds("2,3,4");
        templateData.add(template);

        ExcelUtils.exportExcel(response, templateData, RoleExcelDTO.class, "角色导入模板", "模板说明");
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除角色")
    @RequirePermission("role:delete")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = roleService.deleteRolesByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除角色失败");
    }
}