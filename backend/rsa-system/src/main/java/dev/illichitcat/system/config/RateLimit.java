package dev.illichitcat.system.config;

import java.lang.annotation.*;

/**
 * 限流注解
 * 用于标记需要进行限流的接口方法
 * 支持基于用户ID或IP地址的限流策略
 *
 * @author Illichitcat
 * @since 2026/01/13
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key的前缀
     * 用于在Redis中区分不同的限流维度
     */
    String key() default "rate_limit:";

    /**
     * 时间窗口（秒）
     * 默认60秒
     */
    int time() default 60;

    /**
     * 时间窗口内允许的最大请求数
     * 默认100次
     */
    int count() default 100;

    /**
     * 限流类型
     * DEFAULT: 默认限流（基于IP）
     * USER: 基于用户ID限流
     * IP: 基于IP地址限流
     */
    LimitType limitType() default LimitType.DEFAULT;

    /**
     * 限流类型枚举
     *
     * @author Illichitcat
     * @since 2026/01/13
     */
    enum LimitType {
        /**
         * 默认限流（基于IP）
         */
        DEFAULT,
        /**
         * 基于用户ID限流
         */
        USER,
        /**
         * 基于IP地址限流
         */
        IP
    }
}