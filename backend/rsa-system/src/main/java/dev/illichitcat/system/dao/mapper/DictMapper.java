package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {
}
