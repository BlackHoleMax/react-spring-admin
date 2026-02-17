package dev.illichitcat.generator.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.generator.model.entity.GenTable;
import dev.illichitcat.generator.service.GenTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Tag(name = "代码生成管理")
@RestController
@RequestMapping("/api/gen")
public class GenTableController {

    @Autowired
    private GenTableService genTableService;

    /**
     * 查询数据库表列表
     */
    @Operation(summary = "查询数据库表列表")
    @GetMapping("/db/list")
    public Result<List<GenTable>> listDbTables(@RequestParam(required = false) String tableName) {
        List<GenTable> list = genTableService.selectDbTableList(tableName);
        return Result.ok(list);
    }

    /**
     * 查询代码生成配置列表
     */
    @Operation(summary = "查询代码生成配置列表")
    @GetMapping("/list")
    public Result<List<GenTable>> list(GenTable genTable) {
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return Result.ok(list);
    }

    /**
     * 导入表结构
     */
    @Operation(summary = "导入表结构")
    @PostMapping("/importTable")
    public Result<Void> importTable(@RequestParam String tables) {
        String[] tableNames = tables.split(",");
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList, "admin");
        return Result.ok();
    }

    /**
     * 查询代码生成配置详情
     */
    @Operation(summary = "查询代码生成配置详情")
    @GetMapping("/{tableId}")
    public Result<GenTable> getInfo(@PathVariable Long tableId) {
        GenTable genTable = genTableService.selectGenTableById(tableId);
        return Result.ok(genTable);
    }

    /**
     * 修改代码生成配置
     */
    @Operation(summary = "修改代码生成配置")
    @PutMapping
    public Result<Void> edit(@RequestBody GenTable genTable) {
        genTableService.updateGenTable(genTable);
        return Result.ok();
    }

    /**
     * 删除代码生成配置
     */
    @Operation(summary = "删除代码生成配置")
    @DeleteMapping("/{tableIds}")
    public Result<Void> remove(@PathVariable Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return Result.ok();
    }

    /**
     * 预览代码
     */
    @Operation(summary = "预览代码")
    @GetMapping("/preview/{tableId}")
    public Result<Map<String, String>> preview(@PathVariable Long tableId) {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return Result.ok(dataMap);
    }

    /**
     * 生成代码（下载）
     */
    @Operation(summary = "生成代码（下载）")
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + tableName + ".zip");
        response.getOutputStream().write(data);
    }

    /**
     * 批量生成代码
     */
    @Operation(summary = "批量生成代码")
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, @RequestParam String tables) throws IOException {
        String[] tableNames = tables.split(",");
        byte[] data = genTableService.downloadCode(tableNames);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=rsa.zip");
        response.getOutputStream().write(data);
    }
}