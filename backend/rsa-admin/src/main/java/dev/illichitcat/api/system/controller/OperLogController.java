package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.OperLogExcelDTO;
import dev.illichitcat.system.model.entity.OperLog;
import dev.illichitcat.system.model.query.OperLogQuery;
import dev.illichitcat.system.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "操作日志", description = "操作日志相关接口")
@RestController
@RequestMapping("/api/system/oper-log")
public class OperLogController {

    @Autowired
    private OperLogService operLogService;

    /**
     * 分页查询操作日志列表
     *
     * @param query 操作日志查询条件
     * @return 操作日志分页列表
     */
    @Operation(summary = "分页查询操作日志列表")
    @RequirePermission("operlog:list")
    @GetMapping({"/list"})
    public Result<IPage<OperLog>> list(OperLogQuery query) {
        OperLog operLog = new OperLog();
        operLog.setTitle(query.getTitle());
        operLog.setBusinessType(query.getBusinessType());
        operLog.setOperName(query.getOperName());
        operLog.setStatus(query.getStatus());
        Page<OperLog> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<OperLog> logPage = operLogService.selectOperLogList(page, operLog, query.getStartTime(), query.getEndTime());
        return Result.ok(logPage, toPageInfo(logPage));
    }

    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志信息
     */
    @Operation(summary = "根据ID查询操作日志")
    @RequirePermission("operlog:list")
    @GetMapping("/detail/{id}")
    public Result<OperLog> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        OperLog operLog = operLogService.selectOperLogById(id);
        if (operLog == null) {
            return Result.fail(404, "日志不存在");
        }
        return Result.ok(operLog);
    }

    /**
     * 删除操作日志
     *
     * @param id 日志ID
     * @return 操作结果
     */
    @Operation(summary = "删除操作日志")
    @OperationLog(title = "操作日志", businessType = OperationLog.BusinessType.DELETE)
    @RequirePermission("operlog:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "日志ID") @PathVariable Long id) {
        boolean success = operLogService.deleteOperLogById(id);
        return success ? Result.ok() : Result.fail("删除日志失败");
    }

    /**
     * 批量删除操作日志
     *
     * @param ids 日志ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除操作日志")
    @RequirePermission("operlog:delete")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = operLogService.deleteOperLogsByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除日志失败");
    }

    /**
     * 清空操作日志
     *
     * @return 操作结果
     */
    @Operation(summary = "清空操作日志")
    @RequirePermission("operlog:delete")
    @OperationLog(title = "操作日志", businessType = OperationLog.BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        boolean success = operLogService.cleanOperLog();
        return success ? Result.ok() : Result.fail("清空日志失败");
    }

    /**
     * 导出操作日志
     *
     * @param query    查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Operation(summary = "导出操作日志")
    @RequirePermission("operlog:export")
    @OperationLog(title = "操作日志", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody OperLogQuery query, HttpServletResponse response) {
        OperLog operLog = new OperLog();
        if (query != null) {
            operLog.setTitle(query.getTitle());
            operLog.setBusinessType(query.getBusinessType());
            operLog.setOperName(query.getOperName());
            operLog.setStatus(query.getStatus());
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (query != null && query.getStartTime() != null) {
            startTime = LocalDateTime.parse(query.getStartTime().toString());
        }
        if (query != null && query.getEndTime() != null) {
            endTime = LocalDateTime.parse(query.getEndTime().toString());
        }
        List<OperLogExcelDTO> logList = operLogService.exportOperLogs(operLog, startTime, endTime);
        ExcelUtils.exportExcel(response, logList, OperLogExcelDTO.class, "操作日志", "操作日志列表");
    }

    private Result.PageInfo toPageInfo(IPage<?> page) {
        return new Result.PageInfo()
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }
}