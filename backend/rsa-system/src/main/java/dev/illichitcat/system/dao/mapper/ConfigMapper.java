package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}