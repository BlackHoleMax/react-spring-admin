package dev.illichitcat.generator.service;

import dev.illichitcat.generator.model.entity.GenTable;

import java.util.List;
import java.util.Map;

/**
 * 代码生成业务表Service
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
public interface GenTableService {

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名
     * @return 表列表
     */
    List<GenTable> selectDbTableList(String tableName);

    /**
     * 根据表名列表查询数据库表
     *
     * @param tableNames 表名数组
     * @return 表列表
     */
    List<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询代码生成配置列表
     *
     * @param genTable 代码生成配置
     * @return 代码生成配置列表
     */
    List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 导入表结构
     *
     * @param tableList 表列表
     * @param operName  操作者
     */
    void importGenTable(List<GenTable> tableList, String operName);

    /**
     * 根据表ID查询代码生成配置
     *
     * @param tableId 表ID
     * @return 代码生成配置
     */
    GenTable selectGenTableById(Long tableId);

    /**
     * 修改代码生成配置
     *
     * @param genTable 代码生成配置
     * @return 结果
     */
    int updateGenTable(GenTable genTable);

    /**
     * 删除代码生成配置
     *
     * @param tableIds 表ID数组
     * @return 结果
     */
    int deleteGenTableByIds(Long[] tableIds);

    /**
     * 预览代码
     *
     * @param tableId 表ID
     * @return 预览数据列表
     */
    Map<String, String> previewCode(Long tableId);

    /**
     * 生成代码（下载）
     *
     * @param tableName 表名
     * @return 代码字节数组
     */
    byte[] downloadCode(String tableName);

    /**
     * 批量生成代码
     *
     * @param tableNames 表名数组
     * @return 代码字节数组
     */
    byte[] downloadCode(String[] tableNames);
}