package dev.illichitcat.system.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NoticeWebSocketHandler noticeWebSocketHandler;

    public WebSocketConfig(NoticeWebSocketHandler noticeWebSocketHandler) {
        this.noticeWebSocketHandler = noticeWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(noticeWebSocketHandler, "/ws/notice")
                .setAllowedOriginPatterns("*");
        log.info("[WebSocket] WebSocket端点已注册: /ws/notice");
    }

    /**
     * 应用关闭时清理所有 WebSocket 连接
     */
    @PreDestroy
    public void destroy() {
        log.info("[WebSocket] 应用关闭，开始清理 WebSocket 连接");
        noticeWebSocketHandler.cleanup();
    }
}