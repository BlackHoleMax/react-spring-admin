package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.NoticeWebSocketHandler;
import dev.illichitcat.system.model.vo.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 测试控制器
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Tag(name = "WebSocket测试", description = "WebSocket测试相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/ws-test")
public class WebSocketTestController {

    @Autowired
    private NoticeWebSocketHandler noticeWebSocketHandler;

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    @Operation(summary = "获取在线用户数量")
    @GetMapping("/online-count")
    public Result<Integer> getOnlineCount() {
        int count = noticeWebSocketHandler.getOnlineUserCount();
        log.info("当前在线用户数量: {}", count);
        return Result.ok(count);
    }

    /**
     * 检查 WebSocket 配置
     *
     * @return 配置信息
     */
    @Operation(summary = "检查 WebSocket 配置")
    @GetMapping("/check-config")
    public Result<Map<String, Object>> checkConfig() {
        Map<String, Object> config = new HashMap<>(8);
        config.put("websocketEnabled", true);
        config.put("endpoint", "/ws/notice");
        config.put("allowedOrigins", "*");
        config.put("onlineUserCount", noticeWebSocketHandler.getOnlineUserCount());
        log.info("WebSocket配置检查，在线用户数: {}", noticeWebSocketHandler.getOnlineUserCount());
        return Result.ok(config);
    }

    /**
     * 发送测试通知
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "发送测试通知")
    @PostMapping("/send-test/{userId}")
    public Result<Void> sendTestNotice(@PathVariable Long userId) {
        log.info("发送测试通知，用户ID: {}", userId);

        if (!noticeWebSocketHandler.isUserOnline(userId)) {
            log.warn("用户 {} 不在线，无法发送测试通知", userId);
            return Result.fail("用户不在线，请先建立WebSocket连接");
        }

        NoticeVO notice = new NoticeVO();
        notice.setId(0L);
        notice.setTitle("测试通知");
        notice.setContent("这是一条测试通知，用于验证WebSocket推送功能");
        notice.setType(1);
        notice.setTypeName("系统公告");
        notice.setPriority(1);
        notice.setPriorityName("普通");
        notice.setStatus(2);
        notice.setPublishTime(LocalDateTime.now());
        notice.setPublisherName("系统");
        notice.setReadStatus(0);

        noticeWebSocketHandler.sendNoticeToUser(userId, notice);

        return Result.ok();
    }


}