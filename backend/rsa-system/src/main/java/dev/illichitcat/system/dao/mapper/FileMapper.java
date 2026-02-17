package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理Mapper接口
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {
}