package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.model.entity.StorageConfig;
import dev.illichitcat.system.model.query.StorageConfigQuery;
import dev.illichitcat.system.model.vo.StorageConfigVO;
import dev.illichitcat.system.service.StorageConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 存储配置控制器
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Tag(name = "存储配置")
@RestController
@RequestMapping("/api/system/storage-config")
@Slf4j
public class StorageConfigController {

    @Autowired
    private StorageConfigService storageConfigService;

    @Operation(summary = "分页查询存储配置列表")
    @GetMapping("/page")
    public Result<IPage<StorageConfigVO>> getConfigPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            StorageConfigQuery query) {
        try {
            IPage<StorageConfigVO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
            IPage<StorageConfigVO> result = storageConfigService.selectConfigPage(page, query);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("查询存储配置列表失败", e);
            return Result.fail("查询存储配置列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据ID获取存储配置详情")
    @GetMapping("/{id}")
    public Result<StorageConfigVO> getConfigById(@PathVariable Long id) {
        try {
            StorageConfigVO config = storageConfigService.getConfigById(id);
            if (config == null) {
                return Result.fail("配置不存在");
            }
            return Result.ok(config);
        } catch (Exception e) {
            log.error("获取存储配置详情失败", e);
            return Result.fail("获取存储配置详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增存储配置")
    @PostMapping
    public Result<Void> saveConfig(@RequestBody StorageConfig config) {
        try {
            boolean success = storageConfigService.saveConfig(config);
            return success ? Result.ok() : Result.fail("新增失败");
        } catch (Exception e) {
            log.error("新增存储配置失败", e);
            return Result.fail("新增存储配置失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新存储配置")
    @PutMapping
    public Result<Void> updateConfig(@RequestBody StorageConfig config) {
        try {
            boolean success = storageConfigService.updateConfig(config);
            return success ? Result.ok() : Result.fail("更新失败");
        } catch (Exception e) {
            log.error("更新存储配置失败", e);
            return Result.fail("更新存储配置失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除存储配置")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        try {
            boolean success = storageConfigService.deleteConfig(id);
            return success ? Result.ok() : Result.fail("删除失败");
        } catch (Exception e) {
            log.error("删除存储配置失败", e);
            return Result.fail("删除存储配置失败: " + e.getMessage());
        }
    }

    @Operation(summary = "设置默认配置")
    @PutMapping("/default/{id}")
    public Result<Void> setDefaultConfig(@PathVariable Long id) {
        try {
            boolean success = storageConfigService.setDefaultConfig(id);
            return success ? Result.ok() : Result.fail("设置失败");
        } catch (Exception e) {
            log.error("设置默认配置失败", e);
            return Result.fail("设置默认配置失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取默认配置")
    @GetMapping("/default")
    public Result<StorageConfigVO> getDefaultConfig() {
        try {
            StorageConfigVO config = storageConfigService.getDefaultConfig();
            return Result.ok(config);
        } catch (Exception e) {
            log.error("获取默认配置失败", e);
            return Result.fail("获取默认配置失败: " + e.getMessage());
        }
    }
}