package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface OperLogMapper extends BaseMapper<OperLog> {
}
