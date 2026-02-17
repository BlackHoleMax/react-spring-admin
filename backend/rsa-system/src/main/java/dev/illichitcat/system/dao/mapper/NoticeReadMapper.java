package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.NoticeRead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知已读记录Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<NoticeRead> {
}