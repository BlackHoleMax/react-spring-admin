package dev.illichitcat.quartz.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.quartz.model.entity.JobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务调度日志Mapper接口
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {

}