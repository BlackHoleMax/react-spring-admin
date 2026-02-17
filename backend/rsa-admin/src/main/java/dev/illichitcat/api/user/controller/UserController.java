package dev.illichitcat.api.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.UserDTO;
import dev.illichitcat.system.model.dto.UserExcelDTO;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.model.query.UserQuery;
import dev.illichitcat.system.model.vo.UserVO;
import dev.illichitcat.system.service.PermissionService;
import dev.illichitcat.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/user")
@Validated
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询用户列表
     *
     * @param query 用户查询条件
     * @return 用户分页列表
     */
    @Operation(summary = "分页查询用户列表")
    @RequirePermission("user:list")
    @GetMapping("/list")
    public Result<IPage<UserVO>> list(UserQuery query) {
        User user = new User();
        user.setUsername(query.getUsername());
        user.setNickname(query.getNickname());
        user.setStatus(query.getStatus());
        Page<User> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<User> userPage = userService.selectUserList(page, user);

        // 转换为VO
        IPage<UserVO> voPage = userPage.convert(this::toVO);
        return Result.ok(voPage, toPageInfo(userPage));
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID查询用户")
    @RequirePermission("user:list")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.selectUserById(id);
        if (user == null) {
            return Result.fail(ExceptionCodes.NOT_FOUND, "用户不存在");
        }
        return Result.ok(toVO(user));
    }

    /**
     * 新增用户
     *
     * @param dto 用户信息
     * @return 操作结果
     */
    @Operation(summary = "新增用户")
    @RequirePermission("user:add")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Valid @RequestBody UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        boolean success = userService.insertUser(user);
        return success ? Result.ok() : Result.fail("新增用户失败");
    }

    /**
     * 更新用户
     *
     * @param dto 用户信息
     * @return 操作结果
     */
    @Operation(summary = "更新用户")
    @RequirePermission("user:edit")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(ExceptionCodes.PARAM_ERROR, "用户ID不能为空");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        boolean success = userService.updateUser(user);
        return success ? Result.ok() : Result.fail("更新用户失败");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户")
    @RequirePermission("user:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean success = userService.deleteUserById(id);
        return success ? Result.ok() : Result.fail("删除用户失败");
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除用户")
    @RequirePermission("user:delete")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = userService.deleteUsersByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除用户失败");
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     * @return 操作结果
     */
    @Operation(summary = "更新用户状态")
    @RequirePermission("user:edit")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态") @PathVariable Integer status) {
        boolean success = userService.updateUserStatus(id, status);
        return success ? Result.ok() : Result.fail("更新状态失败");
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "重置用户密码")
    @RequirePermission("user:edit")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping("/{id}/reset-password")
    public Result<String> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id) {
        String newPassword = userService.resetPassword(id);
        return Result.ok(newPassword);
    }

    /**
     * 修改用户密码
     *
     * @param id       用户ID
     * @param password 新密码
     * @return 操作结果
     */
    @Operation(summary = "修改用户密码")
    @RequirePermission("user:edit")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping("/{id}/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam String password) {
        boolean success = userService.changePassword(id, password);
        return success ? Result.ok() : Result.fail("修改密码失败");
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private Result.PageInfo toPageInfo(IPage<?> page) {
        return new Result.PageInfo()
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }

    /**
     * 导出用户数据
     *
     * @param ids 用户ID数组，为空则导出所有
     */
    @Operation(summary = "导出用户数据")
    @RequirePermission("user:export")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody(required = false) Long[] ids, HttpServletResponse response) throws IOException {
        List<Long> userIds = ids != null ? Arrays.asList(ids) : null;
        List<UserExcelDTO> userList = userService.exportUsers(userIds);
        ExcelUtils.exportExcel(response, userList, UserExcelDTO.class, "用户数据", "用户列表");
    }

    /**
     * 导入用户数据
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @Operation(summary = "导入用户数据")
    @RequirePermission("user:import")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<UserExcelDTO> userList = ExcelUtils.importExcel(file, UserExcelDTO.class);
            String result = userService.importUsers(userList);
            return Result.ok(result);
        } catch (IOException e) {
            log.error("导入用户数据失败", e);
            return Result.fail("文件解析失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("导入用户数据失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载用户导入模板
     */
    @Operation(summary = "下载用户导入模板")
    @RequirePermission("user:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个示例数据作为模板
        List<UserExcelDTO> templateData = new ArrayList<>();
        UserExcelDTO template = new UserExcelDTO();
        template.setUsername("zhangsan");
        template.setPassword("123456");
        template.setNickname("张三");
        template.setEmail("zhangsan@example.com");
        template.setPhone("13800138000");
        template.setStatus(1);
        template.setRoleCodes("user,admin");
        templateData.add(template);

        ExcelUtils.exportExcel(response, templateData, UserExcelDTO.class, "用户导入模板", "模板说明");
    }

    /**
     * 获取当前登录用户的权限列表
     *
     * @return 权限标识列表
     */
    @Operation(summary = "获取当前登录用户的权限列表")
    @GetMapping("/permissions")
    public Result<List<String>> getPermissions(@RequestAttribute Long userId) {
        List<String> permissions = permissionService.selectPermissionsByUserId(userId)
                .stream()
                .map(dev.illichitcat.system.model.entity.Permission::getPerm)
                .toList();
        return Result.ok(permissions);
    }
}