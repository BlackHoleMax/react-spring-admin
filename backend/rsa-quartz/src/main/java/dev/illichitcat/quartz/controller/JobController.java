package dev.illichitcat.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.quartz.model.entity.Job;
import dev.illichitcat.quartz.model.vo.JobExportVO;
import dev.illichitcat.quartz.model.vo.JobImportVO;
import dev.illichitcat.quartz.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务调度控制器
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Tag(name = "定时任务管理", description = "定时任务管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/job")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * 分页查询定时任务列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param job     查询条件
     * @return 定时任务分页列表
     */
    @Operation(summary = "分页查询定时任务列表")
    @GetMapping("/list")
    public Result<IPage<Job>> list(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            Job job) {
        Page<Job> page = new Page<>(current, size);
        IPage<Job> jobPage = jobService.selectJobPage(page, job);
        return Result.ok(jobPage);
    }

    /**
     * 根据ID查询定时任务详情
     *
     * @param jobId 任务ID
     * @return 定时任务详情
     */
    @Operation(summary = "根据ID查询定时任务详情")
    @GetMapping("/{jobId}")
    public Result<Job> getById(@Parameter(description = "任务ID") @PathVariable Long jobId) {
        Job job = jobService.selectJobById(jobId);
        if (job == null) {
            return Result.fail("定时任务不存在");
        }
        return Result.ok(job);
    }

    /**
     * 新增定时任务
     *
     * @param job 定时任务信息
     * @return 是否成功
     */
    @Operation(summary = "新增定时任务")
    @PostMapping
    public Result<Void> add(@RequestBody Job job) throws SchedulerException {
        int result = jobService.insertJob(job);
        return result > 0 ? Result.ok() : Result.fail("新增失败");
    }

    /**
     * 修改定时任务
     *
     * @param job 定时任务信息
     * @return 是否成功
     */
    @Operation(summary = "修改定时任务")
    @PutMapping
    public Result<Void> edit(@RequestBody Job job) throws SchedulerException {
        int result = jobService.updateJob(job);
        return result > 0 ? Result.ok() : Result.fail("修改失败");
    }

    /**
     * 删除定时任务
     *
     * @param jobId 任务ID
     * @return 是否成功
     */
    @Operation(summary = "删除定时任务")
    @DeleteMapping("/{jobId}")
    public Result<Void> remove(@Parameter(description = "任务ID") @PathVariable Long jobId) throws SchedulerException {
        int result = jobService.deleteJob(jobId, "DEFAULT");
        return result > 0 ? Result.ok() : Result.fail("删除失败");
    }

    /**
     * 批量删除定时任务
     *
     * @param jobIds 任务ID列表
     * @return 是否成功
     */
    @Operation(summary = "批量删除定时任务")
    @DeleteMapping("/batch/{jobIds}")
    public Result<Void> removeBatch(@Parameter(description = "任务ID列表") @PathVariable Long[] jobIds) throws SchedulerException {
        int result = jobService.deleteJobByIds(jobIds, "DEFAULT");
        return result > 0 ? Result.ok() : Result.fail("删除失败");
    }

    /**
     * 任务调度状态修改
     *
     * @param job 定时任务信息
     * @return 是否成功
     */
    @Operation(summary = "任务调度状态修改")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody Job job) throws SchedulerException {
        int result = jobService.changeStatus(job);
        return result > 0 ? Result.ok() : Result.fail("状态修改失败");
    }

    /**
     * 任务调度立即执行一次
     *
     * @param job 定时任务信息
     * @return 是否成功
     */
    @Operation(summary = "任务调度立即执行一次")
    @PutMapping("/run")
    public Result<Void> run(@RequestBody Job job) throws SchedulerException {
        int result = jobService.run(job);
        return result > 0 ? Result.ok() : Result.fail("执行失败");
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 是否有效
     */
    @Operation(summary = "校验cron表达式是否有效")
    @GetMapping("/checkCronExpression")
    public Result<Boolean> checkCronExpression(@Parameter(description = "cron表达式") @RequestParam String cronExpression) {
        boolean isValid = jobService.checkCronExpressionIsValid(cronExpression);
        return Result.ok(isValid);
    }

    /**
     * 导出定时任务
     *
     * @param job      查询条件
     * @param response HTTP 响应对象
     */
    @Operation(summary = "导出定时任务")
    @PostMapping("/export")
    public void export(Job job, HttpServletResponse response) {
        List<Job> jobList = jobService.exportJobList(job);

        List<JobExportVO> exportVOList = jobList.stream()
                .map(j -> {
                    JobExportVO vo = new JobExportVO();
                    BeanUtils.copyProperties(j, vo);
                    vo.setStatus("0".equals(j.getStatus()) ? "正常" : "暂停");
                    vo.setConcurrent("0".equals(j.getConcurrent()) ? "允许" : "禁止");
                    return vo;
                })
                .collect(Collectors.toList());

        ExcelUtils.exportExcel(
                response,
                exportVOList,
                JobExportVO.class,
                "定时任务_" + System.currentTimeMillis(),
                "定时任务"
        );
    }

    /**
     * 导入定时任务
     *
     * @param file 上传的文件
     * @return 导入结果
     */
    @Operation(summary = "导入定时任务")
    @PostMapping("/import")
    public Result<String> importJob(@Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file) {
        try {
            List<JobImportVO> importVOList = new ArrayList<>();
            ExcelUtils.importExcel(
                    file,
                    JobImportVO.class,
                    ExcelUtils.createSimpleReadListener(importVOList)
            );

            if (importVOList.isEmpty()) {
                return Result.fail("文件中没有数据");
            }

            // 转换为实体对象
            List<Job> jobList = importVOList.stream()
                    .map(vo -> {
                        Job job = new Job();
                        BeanUtils.copyProperties(vo, job);
                        return job;
                    })
                    .collect(Collectors.toList());

            // 批量导入
            int successCount = jobService.importJobList(jobList);
            return Result.ok("成功导入 " + successCount + " 条数据，失败 " + (importVOList.size() - successCount) + " 条");
        } catch (Exception e) {
            log.error("导入定时任务失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }
}