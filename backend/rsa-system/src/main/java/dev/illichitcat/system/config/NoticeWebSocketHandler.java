package dev.illichitcat.system.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.illichitcat.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知 WebSocket 处理器
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Slf4j
@Component
public class NoticeWebSocketHandler extends TextWebSocketHandler {

    /**
     * WebSocket查询参数名
     */
    private static final String TOKEN_PARAM = "token=";

    /**
     * 消息类型：连接
     */
    private static final String MESSAGE_TYPE_CONNECTED = "connected";

    /**
     * 消息类型：心跳请求
     */
    private static final String MESSAGE_TYPE_PING = "ping";

    /**
     * 消息类型：心跳响应
     */
    private static final String MESSAGE_TYPE_PONG = "pong";

    /**
     * 消息字段：类型
     */
    private static final String MESSAGE_FIELD_TYPE = "type";

    /**
     * 消息字段：消息内容
     */
    private static final String MESSAGE_FIELD_MESSAGE = "message";

    /**
     * 消息字段：用户ID
     */
    private static final String MESSAGE_FIELD_USER_ID = "userId";

    /**
     * URL参数分隔符
     */
    private static final String URL_PARAM_SEPARATOR = "&";

    /**
     * URL键值对分隔符
     */
    private static final String URL_KEY_VALUE_SEPARATOR = "=";

    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;

    public NoticeWebSocketHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        // 配置 ObjectMapper 以支持 Java 8 日期时间类型
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        try {
            String query = Objects.requireNonNull(session.getUri()).getQuery();

            if (query != null && query.contains(TOKEN_PARAM)) {
                String token = extractToken(query);
                if (token == null || token.isEmpty()) {
                    log.error("WebSocket连接失败：token为空，SessionID: {}", session.getId());
                    session.close();
                    return;
                }

                // 验证 token
                if (!jwtUtil.validateToken(token)) {
                    log.error("WebSocket连接失败：token无效，SessionID: {}", session.getId());
                    session.close();
                    return;
                }

                // 获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId == null) {
                    log.error("WebSocket连接失败：无法从token获取用户ID，SessionID: {}", session.getId());
                    session.close();
                    return;
                }

                userSessions.put(userId, session);
                log.info("WebSocket连接建立成功，用户ID: {}, SessionID: {}, 当前在线用户数: {}",
                        userId, session.getId(), userSessions.size());

                // 发送连接成功消息
                try {
                    String message = objectMapper.writeValueAsString(
                            Map.of(MESSAGE_FIELD_TYPE, MESSAGE_TYPE_CONNECTED, MESSAGE_FIELD_MESSAGE, "WebSocket连接成功", MESSAGE_FIELD_USER_ID, userId)
                    );
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("发送连接成功消息失败，用户ID: {}, SessionID: {}", userId, session.getId(), e);
                }
            } else {
                log.error("WebSocket连接失败：缺少token参数，SessionID: {}", session.getId());
                session.close();
            }
        } catch (Exception e) {
            log.error("WebSocket连接建立失败，SessionID: {}", session.getId(), e);
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) throws Exception {
        log.error("WebSocket传输错误，SessionID: {}", session.getId(), exception);
        if (session.isOpen()) {
            session.close();
        }
        userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();

        try {
            // 解析消息
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {
            });
            String type = (String) data.get(MESSAGE_FIELD_TYPE);

            // 处理心跳消息
            if (MESSAGE_TYPE_PING.equals(type)) {
                // 响应心跳
                String pongMessage = objectMapper.writeValueAsString(Map.of(MESSAGE_FIELD_TYPE, MESSAGE_TYPE_PONG));
                session.sendMessage(new TextMessage(pongMessage));
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败，SessionID: {}", session.getId(), e);
        }
    }

    /**
     * 向指定用户发送通知
     *
     * @param userId 用户ID
     * @param notice 通知信息
     */
    public void sendNoticeToUser(Long userId, Object notice) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(notice);
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送通知失败，用户ID: {}, SessionID: {}", userId, session.getId(), e);
                // 发送失败时关闭连接
                try {
                    session.close();
                } catch (IOException ex) {
                    log.error("关闭WebSocket连接失败", ex);
                }
                userSessions.remove(userId);
            }
        }
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 从查询字符串中提取 token
     *
     * @param query 查询字符串
     * @return token
     */
    private String extractToken(String query) {
        String[] params = query.split(URL_PARAM_SEPARATOR);
        for (String param : params) {
            String[] keyValue = param.split(URL_KEY_VALUE_SEPARATOR);
            if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }

    /**
     * 清理所有 WebSocket 连接
     * 在应用关闭时调用，优雅地关闭所有连接
     */
    public void cleanup() {
        log.info("开始清理所有 WebSocket 连接，当前在线用户数: {}", userSessions.size());
        int closedCount = 0;
        for (Map.Entry<Long, WebSocketSession> entry : userSessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.close(CloseStatus.NORMAL);
                    closedCount++;
                } catch (IOException e) {
                    log.warn("关闭 WebSocket 连接失败，SessionID: {}", session.getId(), e);
                }
            }
        }
        userSessions.clear();
        log.info("WebSocket 连接清理完成，共关闭 {} 个连接", closedCount);
    }
}