package dev.illichitcat.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 角色缓存配置
 *
 * @author Illichitcat
 * @since 2026/01/10
 */
@Data
@Component
@ConfigurationProperties(prefix = "role.cache")
public class RoleCacheProperties {

    /**
     * 是否启用缓存
     */
    private boolean enabled = true;

    /**
     * 缓存键前缀
     */
    private String rolePrefix = "role:";
    private String rolePermsPrefix = "role_perms:";
    private String roleMenusPrefix = "role_menus:";
    private String userRolesPrefix = "user_roles:";

    /**
     * Caffeine 一级缓存配置
     */
    private Caffeine caffeine = new Caffeine();

    /**
     * Redis 二级缓存配置
     */
    private Redis redis = new Redis();

    /**
     * 定时任务配置
     */
    private Scheduled scheduled = new Scheduled();

    /**
     * @author Illichitcat
     * @since 2026/01/10
     */
    @Data
    public static class Caffeine {
        /**
         * 是否启用 Caffeine 本地缓存
         */
        private boolean enabled = true;

        /**
         * 初始容量
         */
        private int initialCapacity = 50;

        /**
         * 最大容量
         */
        private long maximumSize = 500;

        /**
         * 写入后过期时间（分钟）
         */
        private long expireAfterWrite = 30;

        /**
         * 访问后过期时间（分钟）
         */
        private long expireAfterAccess = 15;

        /**
         * 是否启用统计
         */
        private boolean recordStats = true;

        /**
         * 是否启用异步刷新
         */
        private boolean refreshAfterWrite = true;

        /**
         * 异步刷新间隔（分钟）
         */
        private long refreshAfterWriteMinutes = 25;
    }

    @Data
    public static class Redis {
        /**
         * 是否启用 Redis 二级缓存
         */
        private boolean enabled = true;

        /**
         * 过期时间（分钟）
         */
        private long expireTime = 60;
    }

    @Data
    public static class Scheduled {
        /**
         * 是否启用定时刷新
         */
        private boolean enabled = true;

        /**
         * 缓存预热cron表达式
         */
        private String warmUpCron = "0 0 3 * * ?";
    }
}