package dev.illichitcat.generator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.generator.model.entity.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成业务表字段Mapper
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Mapper
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {

    /**
     * 根据表ID查询列信息
     *
     * @param tableId 表ID
     * @return 列信息列表
     */
    List<GenTableColumn> selectListByTableId(@Param("tableId") Long tableId);

    /**
     * 根据表名查询列信息
     *
     * @param tableName 表名
     * @return 列信息列表
     */
    List<GenTableColumn> selectDbTableColumnsByName(@Param("tableName") String tableName);
}