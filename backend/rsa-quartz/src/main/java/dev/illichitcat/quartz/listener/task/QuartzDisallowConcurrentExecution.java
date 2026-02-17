package dev.illichitcat.quartz.listener.task;

import dev.illichitcat.quartz.model.entity.Job;
import dev.illichitcat.quartz.utils.JobInvokeUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * 禁止并发执行的定时任务
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Slf4j
@Component("quartzDisallowConcurrentExecution")
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {

    @Override
    protected void doExecute(JobExecutionContext context, Job job) throws Exception {
        log.info("禁止并发执行：{}，任务分组：{}", job.getJobName(), job.getJobGroup());
        JobInvokeUtil.invokeMethod(job);
    }
}