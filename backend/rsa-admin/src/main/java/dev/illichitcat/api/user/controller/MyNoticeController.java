package dev.illichitcat.api.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.query.MyNoticeQuery;
import dev.illichitcat.system.model.vo.NoticeVO;
import dev.illichitcat.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 我的通知控制器
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Tag(name = "我的通知", description = "我的通知相关接口")
@Slf4j
@RestController
@RequestMapping("/api/user/notice")
public class MyNoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 分页查询我的通知列表
     *
     * @param query 查询条件
     * @return 我的通知分页列表
     */
    @Operation(summary = "分页查询我的通知列表")
    @RequirePermission("notice:my:list")
    @GetMapping("/list")
    public Result<IPage<NoticeVO>> list(MyNoticeQuery query, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        Page<NoticeVO> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<NoticeVO> noticePage = noticeService.selectMyNoticePage(page, userId, query);
        return Result.ok(noticePage);
    }

    /**
     * 获取未读通知数量
     *
     * @return 未读数量
     */
    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        Long count = noticeService.getUnreadCount(userId);
        return Result.ok(count);
    }

    /**
     * 标记通知为已读
     *
     * @param noticeId 通知ID
     * @return 是否成功
     */
    @Operation(summary = "标记通知为已读")
    @RequirePermission("notice:my:read")
    @PostMapping("/read/{noticeId}")
    public Result<Void> markAsRead(@Parameter(description = "通知ID") @PathVariable Long noticeId, HttpServletRequest request) {
        log.info("收到标记已读请求, noticeId={}", noticeId);
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.error("用户未登录");
            return Result.fail("用户未登录");
        }
        log.info("当前用户ID, userId={}", userId);
        boolean result = noticeService.markAsRead(noticeId, userId);
        log.info("标记已读结果, result={}", result);
        return result ? Result.ok() : Result.fail("标记失败");
    }

    /**
     * 批量标记通知为已读
     *
     * @param ids 通知ID数组
     * @return 是否成功
     */
    @Operation(summary = "批量标记通知为已读")
    @RequirePermission("notice:my:batch-read")
    @PostMapping("/read/batch")
    public Result<Void> batchMarkAsRead(@RequestBody Long[] ids, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        boolean result = noticeService.batchMarkAsRead(ids, userId);
        return result ? Result.ok() : Result.fail("批量标记失败");
    }

    /**
     * 全部标记为已读
     *
     * @return 是否成功
     */
    @Operation(summary = "全部标记为已读")
    @RequirePermission("notice:my:batch-read")
    @PostMapping("/read/all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        boolean result = noticeService.markAllAsRead(userId);
        return result ? Result.ok() : Result.fail("全部标记失败");
    }
}