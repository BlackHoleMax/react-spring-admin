package dev.illichitcat.api.monitor.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.model.vo.CacheInfoVO;
import dev.illichitcat.system.model.vo.KeyDetailVO;
import dev.illichitcat.system.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缓存监控控制器
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Tag(name = "缓存监控")
@RestController
@RequestMapping("/api/monitor/cache")
@Slf4j
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Operation(summary = "获取缓存监控信息")
    @GetMapping("/info")
    public Result<CacheInfoVO> getCacheInfo() {
        try {
            CacheInfoVO cacheInfo = cacheService.getCacheInfo();
            return Result.ok(cacheInfo);
        } catch (Exception e) {
            log.error("获取缓存信息失败", e);
            return Result.fail("获取缓存信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取缓存键列表")
    @GetMapping("/keys")
    public Result<List<String>> getKeys(@RequestParam(defaultValue = "*") String pattern) {
        try {
            List<String> keys = cacheService.getKeys(pattern);
            return Result.ok(keys);
        } catch (Exception e) {
            log.error("获取缓存键列表失败", e);
            return Result.fail("获取缓存键列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取键值")
    @GetMapping("/value/{key}")
    public Result<Object> getValue(@PathVariable String key) {
        try {
            Object value = cacheService.getValue(key);
            return Result.ok(value);
        } catch (Exception e) {
            log.error("获取键值失败", e);
            return Result.fail("获取键值失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取键详细信息")
    @GetMapping("/detail/{key}")
    public Result<KeyDetailVO> getKeyDetail(@PathVariable String key) {
        try {
            KeyDetailVO detail = cacheService.getKeyDetail(key);
            return Result.ok(detail);
        } catch (Exception e) {
            log.error("获取键详细信息失败", e);
            return Result.fail("获取键详细信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除键")
    @DeleteMapping("/key/{key}")
    public Result<Void> deleteKey(@PathVariable String key) {
        try {
            cacheService.deleteKey(key);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除键失败", e);
            return Result.fail("删除键失败: " + e.getMessage());
        }
    }

    @Operation(summary = "设置过期时间")
    @PutMapping("/ttl/{key}")
    public Result<Void> setTtl(@PathVariable String key, @RequestParam Long ttl) {
        try {
            cacheService.setTtl(key, ttl);
            return Result.ok();
        } catch (Exception e) {
            log.error("设置过期时间失败", e);
            return Result.fail("设置过期时间失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清空数据库")
    @DeleteMapping("/clear")
    public Result<Void> clearDb() {
        try {
            cacheService.clearDb();
            return Result.ok();
        } catch (Exception e) {
            log.error("清空数据库失败", e);
            return Result.fail("清空数据库失败: " + e.getMessage());
        }
    }
}