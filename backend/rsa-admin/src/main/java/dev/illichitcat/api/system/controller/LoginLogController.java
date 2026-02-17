package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.entity.LoginLog;
import dev.illichitcat.system.model.query.LoginLogQuery;
import dev.illichitcat.system.model.vo.LoginLogExportVO;
import dev.illichitcat.system.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "登录日志", description = "登录日志相关接口")
@RestController
@RequestMapping("/api/system/login-log")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 分页查询登录日志列表
     *
     * @param query 登录日志查询条件
     * @return 登录日志分页列表
     */
    @Operation(summary = "分页查询登录日志列表")
    @RequirePermission("log:list")
    @GetMapping("/list")
    public Result<IPage<LoginLog>> list(LoginLogQuery query) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(query.getUsername());
        loginLog.setIp(query.getIp());
        loginLog.setStatus(query.getStatus());
        Page<LoginLog> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<LoginLog> logPage = loginLogService.selectLoginLogList(page, loginLog, query.getStartTime(), query.getEndTime());
        return Result.ok(logPage, toPageInfo(logPage));
    }

    /**
     * 根据ID查询登录日志
     *
     * @param id 日志ID
     * @return 登录日志信息
     */
    @Operation(summary = "根据ID查询登录日志")
    @RequirePermission("log:list")
    @GetMapping("/{id}")
    public Result<LoginLog> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        LoginLog loginLog = loginLogService.selectLoginLogById(id);
        if (loginLog == null) {
            return Result.fail(404, "日志不存在");
        }
        return Result.ok(loginLog);
    }

    /**
     * 删除登录日志
     *
     * @param id 日志ID
     * @return 操作结果
     */
    @Operation(summary = "删除登录日志")
    @RequirePermission("log:delete")
    @OperationLog(title = "登录日志", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "日志ID") @PathVariable Long id) {
        boolean success = loginLogService.deleteLoginLogById(id);
        return success ? Result.ok() : Result.fail("删除日志失败");
    }

    /**
     * 批量删除登录日志
     *
     * @param ids 日志ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除登录日志")
    @RequirePermission("log:delete")
    @OperationLog(title = "登录日志", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = loginLogService.deleteLoginLogsByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除日志失败");
    }

    /**
     * 清空登录日志
     *
     * @return 操作结果
     */
    @Operation(summary = "清空登录日志")
    @OperationLog(title = "登录日志", businessType = OperationLog.BusinessType.CLEAN)
    @RequirePermission("log:delete")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        boolean success = loginLogService.cleanLoginLog();
        return success ? Result.ok() : Result.fail("清空日志失败");
    }

    private Result.PageInfo toPageInfo(IPage<?> page) {
        return new Result.PageInfo()
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }

    /**
     * 导出登录日志
     *
     * @param query    查询条件
     * @param response HTTP 响应对象
     */
    @Operation(summary = "导出登录日志")
    @RequirePermission("log:export")
    @OperationLog(title = "登录日志", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(LoginLogQuery query, HttpServletResponse response) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(query.getUsername());
        loginLog.setIp(query.getIp());
        loginLog.setStatus(query.getStatus());

        List<LoginLog> loginLogList = loginLogService.exportLoginLogList(
                loginLog,
                query.getStartTime(),
                query.getEndTime()
        );

        List<LoginLogExportVO> exportVOList = loginLogList.stream()
                .map(log -> {
                    LoginLogExportVO vo = new LoginLogExportVO();
                    BeanUtils.copyProperties(log, vo);
                    vo.setStatus(log.getStatus() == 1 ? "成功" : "失败");
                    return vo;
                })
                .collect(Collectors.toList());

        ExcelUtils.exportExcel(
                response,
                exportVOList,
                LoginLogExportVO.class,
                "登录日志_" + System.currentTimeMillis(),
                "登录日志"
        );
    }
}