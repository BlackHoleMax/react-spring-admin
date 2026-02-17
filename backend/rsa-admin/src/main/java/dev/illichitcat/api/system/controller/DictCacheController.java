package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.service.DictCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典缓存管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@RestController
@RequestMapping("/api/system/dict/cache")
@Tag(name = "字典缓存管理", description = "字典缓存管理相关接口")
public class DictCacheController {

    @Autowired
    private DictCacheService dictCacheService;

    /**
     * 清除所有字典缓存
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清除所有字典缓存", description = "清除所有字典相关的缓存数据")
    @PreAuthorize("hasAuthority('system:cache:clear')")
    public Result<Void> clearAllCache() {
        try {
            dictCacheService.evictAllDictCache();
            return Result.ok("清除所有字典缓存成功");
        } catch (Exception e) {
            log.error("清除所有字典缓存失败", e);
            return Result.fail(ExceptionCodes.SYSTEM_ERROR, "清除所有字典缓存失败: " + e.getMessage());
        }
    }

    /**
     * 清除指定字典的缓存
     */
    @DeleteMapping("/clear/{dictCode}")
    @Operation(summary = "清除指定字典缓存", description = "根据字典编码清除指定字典的缓存")
    @PreAuthorize("hasAuthority('system:cache:clear')")
    public Result<Void> clearDictCache(@PathVariable String dictCode) {
        try {
            dictCacheService.evictDictCache(dictCode);
            return Result.ok("清除字典缓存成功: " + dictCode);
        } catch (Exception e) {
            log.error("清除字典缓存失败, dictCode={}", dictCode, e);
            return Result.fail(ExceptionCodes.SYSTEM_ERROR, "清除字典缓存失败: " + e.getMessage());
        }
    }

    /**
     * 预热字典缓存
     */
    @PostMapping("/warm-up")
    @Operation(summary = "预热字典缓存", description = "将所有字典数据加载到缓存中")
    @PreAuthorize("hasAuthority('system:cache:warm-up')")
    public Result<Void> warmUpCache() {
        try {
            dictCacheService.warmUpCache();
            return Result.ok("字典缓存预热成功");
        } catch (Exception e) {
            log.error("字典缓存预热失败", e);
            return Result.fail(ExceptionCodes.SYSTEM_ERROR, "字典缓存预热失败: " + e.getMessage());
        }
    }

    /**
     * 手动刷新字典缓存
     */
    @PostMapping("/refresh/{dictCode}")
    @Operation(summary = "刷新字典缓存", description = "手动刷新指定字典的缓存")
    @PreAuthorize("hasAuthority('system:cache:refresh')")
    public Result<Void> refreshDict(@PathVariable String dictCode) {
        try {
            dictCacheService.evictDictCache(dictCode);
            return Result.ok("刷新字典缓存成功: " + dictCode);
        } catch (Exception e) {
            log.error("刷新字典缓存失败, dictCode={}", dictCode, e);
            return Result.fail(ExceptionCodes.SYSTEM_ERROR, "刷新字典缓存失败: " + e.getMessage());
        }
    }
}