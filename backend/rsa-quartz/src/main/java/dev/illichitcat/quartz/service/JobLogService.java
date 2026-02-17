package dev.illichitcat.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.quartz.model.entity.JobLog;

/**
 * 定时任务调度日志服务接口
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
public interface JobLogService extends IService<JobLog> {

    /**
     * 分页查询定时任务日志列表
     *
     * @param page   分页对象
     * @param jobLog 查询条件
     * @return 定时任务日志分页结果
     */
    IPage<JobLog> selectJobLogPage(Page<JobLog> page, JobLog jobLog);

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     * @return 结果
     */
    int addJobLog(JobLog jobLog);

    /**
     * 删除任务日志
     *
     * @param jobLogIds 任务日志ID列表
     * @return 结果
     */
    int deleteJobLogByIds(Long[] jobLogIds);

    /**
     * 清空任务日志
     *
     * @return 结果
     */
    int cleanJobLog();

    /**
     * 导出任务日志列表
     *
     * @param jobLog 查询条件
     * @return 任务日志列表
     */
    java.util.List<dev.illichitcat.quartz.model.entity.JobLog> exportJobLogList(
            dev.illichitcat.quartz.model.entity.JobLog jobLog
    );
}