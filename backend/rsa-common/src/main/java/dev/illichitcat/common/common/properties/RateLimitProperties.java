package dev.illichitcat.common.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 限流配置属性类
 * 用于从application.yml中读取限流相关配置
 *
 * @author Illichitcat
 * @since 2026/01/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    /**
     * 是否启用限流
     */
    private Boolean enabled = true;

    /**
     * 默认时间窗口（秒）
     */
    private Integer time = 60;

    /**
     * 默认时间窗口内允许的最大请求数
     */
    private Integer count = 100;

    /**
     * 是否启用限流告警
     */
    private Boolean alertEnabled = false;

    /**
     * 限流告警阈值（当达到此阈值时触发告警）
     */
    private Integer alertThreshold = 80;
}