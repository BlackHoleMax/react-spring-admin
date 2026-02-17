package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.PageResult;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.UserOnlineDTO;
import dev.illichitcat.system.model.entity.UserOnline;
import dev.illichitcat.system.service.UserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@RestController
@RequestMapping("/api/system/online")
@RequiredArgsConstructor
@Tag(name = "在线用户管理", description = "在线用户相关接口")
public class UserOnlineController {

    private final UserOnlineService userOnlineService;

    @GetMapping("/page")
    @Operation(summary = "分页查询在线用户")
    @RequirePermission("online:list")
    public Result<PageResult<UserOnlineDTO>> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "IP地址") @RequestParam(required = false) String ip) {

        Page<UserOnline> page = new Page<>(current, size);
        Page<UserOnlineDTO> result = userOnlineService.selectOnlineUserPage(page, username, ip);

        return Result.ok(PageResult.success(result));
    }

    @GetMapping("/count")
    @Operation(summary = "获取在线用户数量")
    @RequirePermission("online:list")
    public Result<Long> getOnlineCount() {
        long count = userOnlineService.getOnlineUserCount();
        return Result.ok(count);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "根据会话ID获取在线用户详情")
    @RequirePermission("online:list")
    public Result<UserOnlineDTO> getBySessionId(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {

        UserOnlineDTO userOnline = userOnlineService.getBySessionId(sessionId);
        if (userOnline == null) {
            return Result.fail("会话不存在或已过期");
        }
        return Result.ok(userOnline);
    }

    @DeleteMapping("/kickout/{sessionId}")
    @Operation(summary = "踢出用户")
    @RequirePermission("online:kickout")
    public Result<Void> kickout(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {

        boolean success = userOnlineService.kickoutBySessionId(sessionId);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail("踢出失败");
        }
    }

    @DeleteMapping("/kickout/batch")
    @Operation(summary = "批量踢出用户")
    @RequirePermission("online:batch:kickout")
    public Result<Void> batchKickout(
            @Parameter(description = "会话ID列表") @RequestBody List<String> sessionIds) {

        if (sessionIds == null || sessionIds.isEmpty()) {
            return Result.fail("请选择要踢出的会话");
        }

        boolean success = userOnlineService.batchKickout(sessionIds);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail("批量踢出失败");
        }
    }

    @DeleteMapping("/kickout/user/{userId}")
    @Operation(summary = "根据用户ID踢出所有会话")
    @RequirePermission("online:kickout")
    public Result<Void> kickoutByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        boolean success = userOnlineService.kickoutByUserId(userId);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail("踢出失败");
        }
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清理过期会话")
    @RequirePermission("online:kickout")
    public Result<Integer> cleanExpiredSessions() {
        int count = userOnlineService.removeExpiredSessions();
        return Result.ok(count);
    }
}