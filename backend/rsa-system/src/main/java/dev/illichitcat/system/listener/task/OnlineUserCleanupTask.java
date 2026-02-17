package dev.illichitcat.system.listener.task;

import dev.illichitcat.system.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 在线用户清理定时任务
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OnlineUserCleanupTask {

    private final UserOnlineService userOnlineService;

    /**
     * 每小时清理一次过期会话
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupExpiredSessions() {
        try {
            int count = userOnlineService.removeExpiredSessions();
            if (count > 0) {
                log.info("定时清理过期会话完成，共清理 {} 个", count);
            }
        } catch (Exception e) {
            log.error("定时清理过期会话失败", e);
        }
    }
}