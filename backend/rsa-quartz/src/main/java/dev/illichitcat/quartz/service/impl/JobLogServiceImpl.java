package dev.illichitcat.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.quartz.dao.mapper.JobLogMapper;
import dev.illichitcat.quartz.model.entity.JobLog;
import dev.illichitcat.quartz.service.JobLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 定时任务调度日志服务实现类
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Service
@Slf4j
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

    @Override
    public IPage<JobLog> selectJobLogPage(Page<JobLog> page, JobLog jobLog) {
        LambdaQueryWrapper<JobLog> wrapper = buildQueryWrapper(jobLog);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addJobLog(JobLog jobLog) {
        jobLog.setCreateTime(LocalDateTime.now());
        return this.baseMapper.insert(jobLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobLogByIds(Long[] jobLogIds) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(jobLogIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanJobLog() {
        return this.baseMapper.delete(null);
    }

    @Override
    public java.util.List<JobLog> exportJobLogList(JobLog jobLog) {
        LambdaQueryWrapper<JobLog> wrapper = buildQueryWrapper(jobLog);
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param jobLog 查询条件
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<JobLog> buildQueryWrapper(JobLog jobLog) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        if (jobLog.getJobName() != null && !jobLog.getJobName().isEmpty()) {
            wrapper.like(JobLog::getJobName, jobLog.getJobName());
        }
        if (jobLog.getJobGroup() != null && !jobLog.getJobGroup().isEmpty()) {
            wrapper.eq(JobLog::getJobGroup, jobLog.getJobGroup());
        }
        if (jobLog.getStatus() != null && !jobLog.getStatus().isEmpty()) {
            wrapper.eq(JobLog::getStatus, jobLog.getStatus());
        }
        wrapper.orderByDesc(JobLog::getJobLogId);
        return wrapper;
    }
}