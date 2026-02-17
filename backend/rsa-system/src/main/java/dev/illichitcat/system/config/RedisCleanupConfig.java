package dev.illichitcat.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Redis 清理配置
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisCleanupConfig {

    /**
     * 应用关闭时是否清空 Redis
     */
    private boolean cleanupOnShutdown = false;
}