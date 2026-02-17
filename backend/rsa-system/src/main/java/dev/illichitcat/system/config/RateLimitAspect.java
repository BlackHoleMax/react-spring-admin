package dev.illichitcat.system.config;

import dev.illichitcat.common.exception.RateLimitException;
import dev.illichitcat.common.utils.IpUtils;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 限流切面
 * 基于Bucket4j实现令牌桶算法的接口限流
 * 支持基于用户ID或IP地址的限流策略
 *
 * @author Illichitcat
 * @since 2026/01/13
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 本地缓存Bucket对象，避免重复创建
     */
    private final ConcurrentMap<String, Bucket> bucketCache = new ConcurrentHashMap<>(256);

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 环绕通知：拦截带有@RateLimit注解的方法
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        String key = buildRateLimitKey(request, rateLimit);
        Bucket bucket = getBucket(key, rateLimit);

        if (!bucket.tryConsume(1)) {
            logger.warn("[RateLimit] uri={}, key={}, 超过限流阈值", request.getRequestURI(), key);
            throw new RateLimitException(buildRateLimitMessage(rateLimit));
        }

        return joinPoint.proceed();
    }

    /**
     * 构建限流Key
     *
     * @param request   HTTP请求对象
     * @param rateLimit 限流注解
     * @return 限流Key
     */
    private String buildRateLimitKey(HttpServletRequest request, RateLimit rateLimit) {
        String prefix = rateLimit.key();
        String uri = request.getRequestURI();
        RateLimit.LimitType limitType = rateLimit.limitType();

        String identifier = switch (limitType) {
            case USER -> getUserIdFromRequest(request);
            case IP -> IpUtils.getClientIp(request);
            default -> IpUtils.getClientIp(request);
        };

        return prefix + identifier + ":" + uri;
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            userId = request.getAttribute("userId") != null
                    ? request.getAttribute("userId").toString()
                    : "anonymous";
        }
        return userId;
    }

    /**
     * 获取或创建Bucket对象
     *
     * @param key       限流Key
     * @param rateLimit 限流注解
     * @return Bucket对象
     */
    private Bucket getBucket(String key, RateLimit rateLimit) {
        return bucketCache.computeIfAbsent(key, k -> createBucket(rateLimit));
    }

    /**
     * 创建Bucket对象（令牌桶算法）
     *
     * @param rateLimit 限流注解
     * @return Bucket对象
     */
    private Bucket createBucket(RateLimit rateLimit) {
        long capacity = rateLimit.count();
        Duration refillDuration = Duration.ofSeconds(rateLimit.time());

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, refillDuration)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 构建限流提示信息
     *
     * @param rateLimit 限流注解
     * @return 限流提示信息
     */
    private String buildRateLimitMessage(RateLimit rateLimit) {
        return String.format("访问过于频繁，请在%d秒后重试", rateLimit.time());
    }

    /**
     * 获取当前请求对象
     *
     * @return HttpServletRequest对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}