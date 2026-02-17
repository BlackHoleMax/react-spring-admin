package dev.illichitcat.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.quartz.model.entity.JobLog;
import dev.illichitcat.quartz.model.vo.JobLogExportVO;
import dev.illichitcat.quartz.service.JobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务调度日志控制器
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Tag(name = "定时任务日志管理", description = "定时任务日志管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/job-log")
public class JobLogController {

    @Autowired
    private JobLogService jobLogService;

    /**
     * 分页查询定时任务日志列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param jobLog  查询条件
     * @return 定时任务日志分页列表
     */
    @Operation(summary = "分页查询定时任务日志列表")
    @GetMapping("/list")
    public Result<IPage<JobLog>> list(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            JobLog jobLog) {
        Page<JobLog> page = new Page<>(current, size);
        IPage<JobLog> jobLogPage = jobLogService.selectJobLogPage(page, jobLog);
        return Result.ok(jobLogPage);
    }

    /**
     * 根据ID查询定时任务日志详情
     *
     * @param jobLogId 任务日志ID
     * @return 定时任务日志详情
     */
    @Operation(summary = "根据ID查询定时任务日志详情")
    @GetMapping("/{jobLogId}")
    public Result<JobLog> getById(@Parameter(description = "任务日志ID") @PathVariable Long jobLogId) {
        JobLog jobLog = jobLogService.getById(jobLogId);
        if (jobLog == null) {
            return Result.fail("定时任务日志不存在");
        }
        return Result.ok(jobLog);
    }

    /**
     * 删除定时任务日志
     *
     * @param jobLogId 任务日志ID
     * @return 是否成功
     */
    @Operation(summary = "删除定时任务日志")
    @DeleteMapping("/{jobLogId}")
    public Result<Void> remove(@Parameter(description = "任务日志ID") @PathVariable Long jobLogId) {
        int result = jobLogService.deleteJobLogByIds(new Long[]{jobLogId});
        return result > 0 ? Result.ok() : Result.fail("删除失败");
    }

    /**
     * 批量删除定时任务日志
     *
     * @param jobLogIds 任务日志ID列表
     * @return 是否成功
     */
    @Operation(summary = "批量删除定时任务日志")
    @DeleteMapping("/batch/{jobLogIds}")
    public Result<Void> removeBatch(@Parameter(description = "任务日志ID列表") @PathVariable Long[] jobLogIds) {
        int result = jobLogService.deleteJobLogByIds(jobLogIds);
        return result > 0 ? Result.ok() : Result.fail("删除失败");
    }

    /**
     * 清空定时任务日志
     *
     * @return 是否成功
     */
    @Operation(summary = "清空定时任务日志")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        int result = jobLogService.cleanJobLog();
        return result > 0 ? Result.ok() : Result.fail("清空失败");
    }

    /**
     * 导出定时任务日志
     *
     * @param jobLog   查询条件
     * @param response HTTP 响应对象
     */
    @Operation(summary = "导出定时任务日志")
    @PostMapping("/export")
    public void export(JobLog jobLog, HttpServletResponse response) {
        List<JobLog> jobLogList = jobLogService.exportJobLogList(jobLog);

        List<JobLogExportVO> exportVOList = jobLogList.stream()
                .map(log -> {
                    JobLogExportVO vo = new JobLogExportVO();
                    BeanUtils.copyProperties(log, vo);
                    vo.setStatus("0".equals(log.getStatus()) ? "正常" : "失败");
                    return vo;
                })
                .collect(Collectors.toList());

        ExcelUtils.exportExcel(
                response,
                exportVOList,
                JobLogExportVO.class,
                "任务日志_" + System.currentTimeMillis(),
                "任务日志"
        );
    }
}