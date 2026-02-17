package dev.illichitcat.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.quartz.model.entity.Job;
import org.quartz.SchedulerException;

/**
 * 定时任务调度服务接口
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
public interface JobService extends IService<Job> {

    /**
     * 分页查询定时任务列表
     *
     * @param page 分页对象
     * @param job  查询条件
     * @return 定时任务分页结果
     */
    IPage<Job> selectJobPage(Page<Job> page, Job job);

    /**
     * 查询所有定时任务
     *
     * @return 定时任务列表
     */
    java.util.List<Job> selectJobAll();

    /**
     * 根据任务ID查询定时任务
     *
     * @param jobId 任务ID
     * @return 定时任务
     */
    Job selectJobById(Long jobId);

    /**
     * 新增任务
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int insertJob(Job job) throws SchedulerException;

    /**
     * 更新任务
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int updateJob(Job job) throws SchedulerException;

    /**
     * 删除任务
     *
     * @param jobId    任务ID
     * @param jobGroup 任务组名
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int deleteJob(Long jobId, String jobGroup) throws SchedulerException;

    /**
     * 批量删除任务
     *
     * @param jobIds   任务ID列表
     * @param jobGroup 任务组名
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int deleteJobByIds(Long[] jobIds, String jobGroup) throws SchedulerException;

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int changeStatus(Job job) throws SchedulerException;

    /**
     * 任务调度立即执行一次
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException 调度异常
     */
    int run(Job job) throws SchedulerException;

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    boolean checkCronExpressionIsValid(String cronExpression);

    /**
     * 导出任务列表
     *
     * @param job 查询条件
     * @return 任务列表
     */
    java.util.List<dev.illichitcat.quartz.model.entity.Job> exportJobList(
            dev.illichitcat.quartz.model.entity.Job job
    );

    /**
     * 批量导入任务
     *
     * @param jobList 任务列表
     * @return 成功导入的数量
     * @throws org.quartz.SchedulerException 调度异常
     */
    int importJobList(java.util.List<dev.illichitcat.quartz.model.entity.Job> jobList) throws org.quartz.SchedulerException;
}