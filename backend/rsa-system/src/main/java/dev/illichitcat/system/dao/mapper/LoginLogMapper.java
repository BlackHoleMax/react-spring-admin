package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
