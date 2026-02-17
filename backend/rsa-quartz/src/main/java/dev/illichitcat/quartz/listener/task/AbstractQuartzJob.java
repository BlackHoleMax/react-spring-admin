package dev.illichitcat.quartz.listener.task;

import dev.illichitcat.common.utils.SpringUtils;
import dev.illichitcat.quartz.model.entity.Job;
import dev.illichitcat.quartz.model.entity.JobLog;
import dev.illichitcat.quartz.service.JobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

/**
 * 抽象quartz调用
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Slf4j
public abstract class AbstractQuartzJob implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Job job = (Job) context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        JobLog jobLog = new JobLog();
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setCreateTime(LocalDateTime.now());

        try {
            before(context, job);
            doExecute(context, job);
            jobLog.setStatus("0");
            jobLog.setStopTime(LocalDateTime.now());
            getJobLogService().addJobLog(jobLog);
        } catch (Exception e) {
            log.error("任务执行异常 - ：", e);
            jobLog.setStatus("1");
            jobLog.setExceptionInfo(e.getMessage());
            jobLog.setStopTime(LocalDateTime.now());
            getJobLogService().addJobLog(jobLog);
        }
    }

    /**
     * 获取 JobLogService
     * 由于 Quartz Job 不是 Spring 管理的 Bean，需要通过 SpringUtils 动态获取
     */
    private JobLogService getJobLogService() {
        return SpringUtils.getBean(JobLogService.class);
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param job     系统计划任务
     */
    protected void before(JobExecutionContext context, Job job) {
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param job     系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, Job job) throws Exception;
}