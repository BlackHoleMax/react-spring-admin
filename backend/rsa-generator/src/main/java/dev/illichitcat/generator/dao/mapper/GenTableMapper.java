package dev.illichitcat.generator.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.generator.model.entity.GenTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成业务表Mapper
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Mapper
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名
     * @return 数据库表列表
     */
    List<GenTable> selectDbTableList(@Param("tableName") String tableName);

    /**
     * 根据表名查询表信息
     *
     * @param tableNames 表名数组
     * @return 表信息列表
     */
    List<GenTable> selectDbTableListByNames(@Param("tableNames") String[] tableNames);
}