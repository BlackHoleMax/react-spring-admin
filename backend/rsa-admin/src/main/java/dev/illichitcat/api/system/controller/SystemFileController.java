package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.query.FileQuery;
import dev.illichitcat.system.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统文件管理控制器
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Tag(name = "系统文件管理")
@RestController
@RequestMapping("/api/system/file")
@RequiredArgsConstructor
public class SystemFileController {

    private final FileService fileService;

    @Operation(summary = "分页查询文件列表")
    @GetMapping("/page")
    public Result<IPage<File>> selectFilePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            FileQuery query) {
        Page<File> page = new Page<>(current, size);
        IPage<File> result = fileService.selectFilePage(page, query);
        return Result.ok(result);
    }

    @Operation(summary = "根据ID删除文件")
    @OperationLog(title = "文件管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> deleteFileById(@PathVariable Long id) {
        boolean success = fileService.deleteFileById(id);
        return success ? Result.ok() : Result.fail("删除失败");
    }

    @Operation(summary = "批量删除文件")
    @OperationLog(title = "文件管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteFileByIds(@RequestBody Long[] ids) {
        boolean success = fileService.deleteFileByIds(ids);
        return success ? Result.ok() : Result.fail("删除失败");
    }
}