package dev.illichitcat.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.quartz.dao.mapper.JobMapper;
import dev.illichitcat.quartz.model.entity.Job;
import dev.illichitcat.quartz.service.JobService;
import dev.illichitcat.quartz.utils.ScheduleUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务调度服务实现类
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Service
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    /**
     * 任务状态 - 正常
     */
    private static final String STATUS_NORMAL = "0";

    /**
     * 任务状态 - 暂停
     */
    private static final String STATUS_PAUSE = "1";

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        try {
            scheduler.clear();
            List<Job> jobList = selectJobAll();
            for (Job job : jobList) {
                ScheduleUtils.createScheduleJob(scheduler, job);
            }
            log.info("定时任务初始化成功，共加载 {} 个任务", jobList.size());
        } catch (Exception e) {
            log.error("定时任务初始化失败: {}", e.getMessage());
            log.error("请检查数据库连接是否正常，或稍后手动启动定时任务");
            // 不抛出异常，允许应用继续启动
        }
    }

    @Override
    public IPage<Job> selectJobPage(Page<Job> page, Job job) {
        LambdaQueryWrapper<Job> wrapper = buildQueryWrapper(job);
        return this.page(page, wrapper);
    }

    @Override
    public List<Job> selectJobAll() {
        return this.list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(Job job) throws SchedulerException {
        job.setStatus("0");
        int rows = this.baseMapper.insert(job);
        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(Job job) throws SchedulerException {
        Job properties = selectJobById(job.getJobId());
        int rows = this.baseMapper.updateById(job);
        if (rows > 0) {
            ScheduleUtils.updateScheduleJob(scheduler, job, properties.getJobGroup());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(Long jobId, String jobGroup) throws SchedulerException {
        int rows = this.baseMapper.deleteById(jobId);
        if (rows > 0) {
            ScheduleUtils.deleteScheduleJob(scheduler, jobId, jobGroup);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobByIds(Long[] jobIds, String jobGroup) throws SchedulerException {
        for (Long jobId : jobIds) {
            deleteJob(jobId, jobGroup);
        }
        return jobIds.length;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(Job job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (STATUS_NORMAL.equals(status)) {
            rows = resumeJob(job);
        } else if (STATUS_PAUSE.equals(status)) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    public int resumeJob(Job job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus("0");
        int rows = this.baseMapper.updateById(job);
        if (rows > 0) {
            ScheduleUtils.resumeJob(scheduler, jobId, jobGroup);
        }
        return rows;
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    public int pauseJob(Job job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus("1");
        int rows = this.baseMapper.updateById(job);
        if (rows > 0) {
            ScheduleUtils.pauseJob(scheduler, jobId, jobGroup);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int run(Job job) throws SchedulerException {
        int rows = 0;
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        Job properties = selectJobById(job.getJobId());
        rows = ScheduleUtils.run(scheduler, properties);
        return rows;
    }

    @Override
    public Job selectJobById(Long jobId) {
        return this.baseMapper.selectById(jobId);
    }

    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return ScheduleUtils.checkCronExpressionIsValid(cronExpression);
    }

    @Override
    public java.util.List<Job> exportJobList(Job job) {
        LambdaQueryWrapper<Job> wrapper = buildQueryWrapper(job);
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param job 查询条件
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<Job> buildQueryWrapper(Job job) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        if (job.getJobName() != null && !job.getJobName().isEmpty()) {
            wrapper.like(Job::getJobName, job.getJobName());
        }
        if (job.getJobGroup() != null && !job.getJobGroup().isEmpty()) {
            wrapper.eq(Job::getJobGroup, job.getJobGroup());
        }
        if (job.getStatus() != null && !job.getStatus().isEmpty()) {
            wrapper.eq(Job::getStatus, job.getStatus());
        }
        wrapper.orderByDesc(Job::getJobId);
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importJobList(List<Job> jobList) throws SchedulerException {
        int successCount = 0;
        for (Job job : jobList) {
            try {
                // 设置默认值
                if (job.getStatus() == null || job.getStatus().isEmpty()) {
                    job.setStatus("1");
                }
                if (job.getMisfirePolicy() == null || job.getMisfirePolicy().isEmpty()) {
                    job.setMisfirePolicy("3");
                }
                if (job.getConcurrent() == null || job.getConcurrent().isEmpty()) {
                    job.setConcurrent("1");
                }

                // 插入数据库
                int rows = this.baseMapper.insert(job);
                if (rows > 0) {
                    // 创建定时任务
                    ScheduleUtils.createScheduleJob(scheduler, job);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("导入任务失败: jobName={}, error={}", job.getJobName(), e.getMessage());
            }
        }
        return successCount;
    }
}