package dev.illichitcat.quartz.utils;

import dev.illichitcat.quartz.listener.task.QuartzDisallowConcurrentExecution;
import dev.illichitcat.quartz.listener.task.QuartzJobExecution;
import dev.illichitcat.quartz.listener.task.ScheduleConstants;
import dev.illichitcat.quartz.model.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * 定时任务工具类
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Slf4j
public class ScheduleUtils {

    /**
     * 任务状态 - 正常
     */
    private static final String STATUS_NORMAL = "0";

    /**
     * 任务状态 - 暂停
     */
    private static final String STATUS_PAUSE = "1";

    /**
     * 得到quartz任务类
     *
     * @param job 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends org.quartz.Job> getQuartzJobClass(Job job) {
        boolean isConcurrent = STATUS_NORMAL.equals(job.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, Job job) throws SchedulerException {
        Class<? extends org.quartz.Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if (STATUS_PAUSE.equals(job.getStatus())) {
            scheduler.pauseJob(getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(Job job, CronScheduleBuilder cb)
            throws SchedulerException {
        return switch (job.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT -> cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES -> cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED -> cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING -> cb.withMisfireHandlingInstructionDoNothing();
            default ->
                    throw new SchedulerException("The task misfire policy '" + job.getMisfirePolicy() + "' cannot be used in cron schedule information");
        };
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, Job job, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(job.getJobId(), jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            throw new SchedulerException("获取定时任务CronTrigger失败");
        }
        CronScheduleBuilder scheduleBuilder = cronSchedule(job.getCronExpression());
        scheduleBuilder = handleCronScheduleMisfirePolicy(job, scheduleBuilder);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 执行一次任务
     */
    public static int run(Scheduler scheduler, Job job) throws SchedulerException {
        JobKey jobKey = getJobKey(job.getJobId(), job.getJobGroup());
        scheduler.triggerJob(jobKey);
        return 1;
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    public static boolean checkCronExpressionIsValid(String cronExpression) {
        try {
            CronScheduleBuilder.cronSchedule(cronExpression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}