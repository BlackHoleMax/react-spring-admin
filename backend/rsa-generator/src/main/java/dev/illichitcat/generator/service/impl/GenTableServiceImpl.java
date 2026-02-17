package dev.illichitcat.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.illichitcat.generator.dao.mapper.GenTableColumnMapper;
import dev.illichitcat.generator.dao.mapper.GenTableMapper;
import dev.illichitcat.generator.model.entity.GenTable;
import dev.illichitcat.generator.model.entity.GenTableColumn;
import dev.illichitcat.generator.service.GenTableService;
import dev.illichitcat.generator.utils.GenUtils;
import dev.illichitcat.generator.utils.VelocityUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成业务表Service实现
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Service
public class GenTableServiceImpl implements GenTableService {

    @Autowired
    private GenTableMapper genTableMapper;

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    @Override
    public List<GenTable> selectDbTableList(String tableName) {
        return genTableMapper.selectDbTableList(tableName);
    }

    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        LambdaQueryWrapper<GenTable> wrapper = new LambdaQueryWrapper<>();
        if (genTable.getTableName() != null) {
            wrapper.like(GenTable::getTableName, genTable.getTableName());
        }
        if (genTable.getTableComment() != null) {
            wrapper.like(GenTable::getTableComment, genTable.getTableComment());
        }
        wrapper.orderByDesc(GenTable::getCreateTime);
        return genTableMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGenTable(List<GenTable> tableList, String operName) {
        try {
            for (GenTable table : tableList) {
                String tableName = table.getTableName();
                GenUtils.initTable(table, operName);
                int row = genTableMapper.insert(table);
                if (row > 0) {
                    List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
                    for (GenTableColumn column : genTableColumns) {
                        GenUtils.initColumnField(column, table);
                        genTableColumnMapper.insert(column);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }

    @Override
    public GenTable selectGenTableById(Long tableId) {
        GenTable genTable = genTableMapper.selectById(tableId);
        if (genTable != null) {
            List<GenTableColumn> columns = genTableColumnMapper.selectListByTableId(tableId);
            genTable.setColumns(columns);
        }
        return genTable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateGenTable(GenTable genTable) {
        int rows = genTableMapper.updateById(genTable);
        if (rows > 0 && genTable.getColumns() != null) {
            for (GenTableColumn column : genTable.getColumns()) {
                genTableColumnMapper.updateById(column);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteGenTableByIds(Long[] tableIds) {
        for (Long tableId : tableIds) {
            genTableColumnMapper.delete(new LambdaQueryWrapper<GenTableColumn>().eq(GenTableColumn::getTableId, tableId));
            genTableMapper.deleteById(tableId);
        }
        return tableIds.length;
    }

    @Override
    public Map<String, String> previewCode(Long tableId) {
        GenTable genTable = selectGenTableById(tableId);
        if (genTable == null) {
            throw new RuntimeException("表不存在");
        }
        Map<String, String> dataMap = new HashMap<>(16);
        dataMap.put("Entity.java", generateEntity(genTable));
        dataMap.put("VO.java", generateVO(genTable));
        dataMap.put("DTO.java", generateDTO(genTable));
        dataMap.put("ExcelDTO.java", generateExcelDTO(genTable));
        dataMap.put("Mapper.java", generateMapper(genTable));
        dataMap.put("Mapper.xml", generateMapperXml(genTable));
        dataMap.put("Service.java", generateService(genTable));
        dataMap.put("ServiceImpl.java", generateServiceImpl(genTable));
        dataMap.put("Controller.java", generateController(genTable));
        dataMap.put("index.tsx", generateFrontend(genTable));
        dataMap.put("service.ts", generateFrontendService(genTable));
        dataMap.put("route.ts", generateRoute(genTable));
        dataMap.put("menu.sql", generateMenuSql(genTable));
        return dataMap;
    }

    @Override
    public byte[] downloadCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            GenTable genTable = genTableMapper.selectOne(
                    new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName)
            );
            if (genTable != null) {
                genTable.setColumns(genTableColumnMapper.selectListByTableId(genTable.getTableId()));
                generateCode(genTable, zip);
            }
        } catch (IOException e) {
            throw new RuntimeException("生成代码失败", e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] downloadCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            for (String tableName : tableNames) {
                GenTable genTable = genTableMapper.selectOne(
                        new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName)
                );
                if (genTable != null) {
                    genTable.setColumns(genTableColumnMapper.selectListByTableId(genTable.getTableId()));
                    generateCode(genTable, zip);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("生成代码失败", e);
        }
        return outputStream.toByteArray();
    }

    /**
     * 生成代码
     *
     * @param genTable 表信息
     * @param zip      压缩流
     */
    private void generateCode(GenTable genTable, ZipOutputStream zip) {
        try {
            VelocityContext context = prepareContext(genTable);
            // 生成后端代码 - java/ 目录
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/model/entity/" + genTable.getClassName() + ".java",
                    VelocityUtils.renderTemplate("templates/java/entity.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/model/vo/" + genTable.getClassName() + "VO.java",
                    VelocityUtils.renderTemplate("templates/java/vo.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/model/dto/" + genTable.getClassName() + "DTO.java",
                    VelocityUtils.renderTemplate("templates/java/dto.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/model/dto/" + genTable.getClassName() + "ExcelDTO.java",
                    VelocityUtils.renderTemplate("templates/java/excelDto.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/dao/mapper/" + genTable.getClassName() + "Mapper.java",
                    VelocityUtils.renderTemplate("templates/java/mapper.java.vm", context));
            addToZip(zip, "resources/mapper/" + genTable.getClassName() + "Mapper.xml",
                    VelocityUtils.renderTemplate("templates/java/mapper.xml.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/service/" + genTable.getClassName() + "Service.java",
                    VelocityUtils.renderTemplate("templates/java/service.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/service/impl/" + genTable.getClassName() + "ServiceImpl.java",
                    VelocityUtils.renderTemplate("templates/java/serviceImpl.java.vm", context));
            addToZip(zip, "java/" + genTable.getPackageName().replace(".", "/") + "/"
                            + "/controller/" + genTable.getClassName() + "Controller.java",
                    VelocityUtils.renderTemplate("templates/java/controller.java.vm", context));
            // 生成前端代码 - react/ 目录
            addToZip(zip, "react/src/pages/" + genTable.getClassName()
                            + "/index.tsx",
                    VelocityUtils.renderTemplate("templates/frontend/index.tsx.vm", context));
            addToZip(zip, "react/src/services/" + genTable.getClassName() + ".ts",
                    VelocityUtils.renderTemplate("templates/frontend/service.ts.vm", context));
            // 生成路由配置代码片段
            addToZip(zip, "react/route_" + genTable.getClassName() + ".ts",
                    VelocityUtils.renderTemplate("templates/frontend/route.ts.vm", context));
            // 生成动态菜单 SQL - sql/ 目录
            addToZip(zip, "sql/" + genTable.getClassName() + "_menu.sql",
                    VelocityUtils.renderTemplate("templates/sql/menu.sql.vm", context));
        } catch (IOException e) {
            throw new RuntimeException("生成代码失败", e);
        }
    }

    /**
     * 添加内容到zip文件
     *
     * @param zip      压缩流
     * @param fileName 文件名
     * @param content  内容
     * @throws IOException IO异常
     */
    private void addToZip(ZipOutputStream zip, String fileName, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(fileName));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    /**
     * 准备Velocity上下文
     *
     * @param genTable 表信息
     * @return Velocity上下文
     */
    private VelocityContext prepareContext(GenTable genTable) {
        VelocityContext context = new VelocityContext();
        context.put("table", genTable);
        context.put("columns", genTable.getColumns());
        context.put("packageName", genTable.getPackageName());
        context.put("className", genTable.getClassName());
        context.put("functionAuthor", genTable.getFunctionAuthor());
        context.put("datetime", java.time.LocalDateTime.now());
        return context;
    }

    /**
     * 以下方法为占位方法，实际生成逻辑将在Velocity模板中实现
     */
    private String generateEntity(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/entity.java.vm", context);
    }

    private String generateVO(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/vo.java.vm", context);
    }

    private String generateDTO(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/dto.java.vm", context);
    }

    private String generateExcelDTO(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/excelDto.java.vm", context);
    }

    private String generateMapper(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/mapper.java.vm", context);
    }

    private String generateMapperXml(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/mapper.xml.vm", context);
    }

    private String generateService(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/service.java.vm", context);
    }

    private String generateServiceImpl(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/serviceImpl.java.vm", context);
    }

    private String generateController(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/java/controller.java.vm", context);
    }

    private String generateFrontend(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/frontend/index.tsx.vm", context);
    }

    private String generateFrontendService(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/frontend/service.ts.vm", context);
    }

    private String generateRoute(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/frontend/route.ts.vm", context);
    }

    private String generateMenuSql(GenTable genTable) {
        VelocityContext context = prepareContext(genTable);
        return VelocityUtils.renderTemplate("templates/sql/menu.sql.vm", context);
    }
}