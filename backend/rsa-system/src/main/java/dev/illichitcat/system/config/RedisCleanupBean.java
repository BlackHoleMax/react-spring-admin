package dev.illichitcat.system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis 清理 Bean
 * 在应用关闭时清空 Redis（如果配置启用）
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCleanupBean implements SmartLifecycle {

    private final StringRedisTemplate redisTemplate;
    private final RedisCleanupConfig cleanupConfig;
    private volatile boolean running = false;

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
        cleanupRedis();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        // 设置为最大值，确保在最后阶段执行
        return Integer.MAX_VALUE;
    }

    /**
     * 应用关闭时执行清理
     * 此方法会在 RedisConnectionFactory 关闭之前执行
     */
    private void cleanupRedis() {
        if (!cleanupConfig.isCleanupOnShutdown()) {
            log.info("应用关闭时清空 Redis 功能未启用");
            return;
        }

        try {
            // 获取 Redis 连接（此时连接工厂还未关闭）
            RedisConnection connection = null;
            if (redisTemplate.getConnectionFactory() != null) {
                connection = RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
            }

            log.info("开始清空 Redis");

            // 获取所有键
            Set<byte[]> keys = null;
            if (connection != null) {
                keys = connection.keyCommands().keys("*".getBytes());
            }
            int dbSize = keys != null ? keys.size() : 0;
            log.info("当前数据库键数量：{}", dbSize);

            // 删除所有键
            if (keys != null && !keys.isEmpty()) {
                connection.keyCommands().del(keys.toArray(new byte[0][0]));
                log.info("Redis 清空完成，已删除 {} 个键", keys.size());
            } else {
                log.info("Redis 当前数据库为空，无需清空");
            }

        } catch (Exception e) {
            log.error("清空 Redis 失败", e);
        } finally {
            // 确保连接被释放
            try {
                if (redisTemplate.getConnectionFactory() != null) {
                    RedisConnectionUtils.releaseConnection(
                            RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory()),
                            redisTemplate.getConnectionFactory()
                    );
                }
            } catch (Exception e) {
                // 忽略释放连接时的异常
            }
        }
    }
}