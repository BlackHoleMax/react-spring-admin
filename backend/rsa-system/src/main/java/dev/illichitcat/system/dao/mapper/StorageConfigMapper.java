package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.StorageConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 存储配置Mapper接口
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Mapper
public interface StorageConfigMapper extends BaseMapper<StorageConfig> {
}