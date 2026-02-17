package dev.illichitcat.system.config;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.properties.SecurityProperties;
import dev.illichitcat.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 权限检查拦截器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final SecurityProperties securityProps;

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是处理方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 对于Swagger等特殊路径，直接放行
        String uri = request.getRequestURI();
        if (isIgnoreUrl(uri)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查是否有RequirePermission注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            // 检查类上是否有RequirePermission注解
            requirePermission = method.getDeclaringClass().getAnnotation(RequirePermission.class);
        }

        // 如果没有权限注解，直接放行
        if (requirePermission == null) {
            return true;
        }

        // 获取需要的权限
        String permission = requirePermission.value();
        if (permission == null || permission.isEmpty()) {
            return true;
        }

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.error("权限检查失败: 用户未登录");
            throw new AuthException(ExceptionCodes.UNAUTHORIZED, "用户未登录");
        }

        String key = "user_perms:" + userId;
        Boolean hasPermission = redisTemplate.opsForSet().isMember(key, permission);
        log.debug("权限检查: 用户ID={}, 需要权限={}, 是否有权限={}", userId, permission, hasPermission);

        if (Boolean.FALSE.equals(hasPermission)) {
            throw new AuthException(ExceptionCodes.FORBIDDEN, "用户权限不足");
        }

        return true;
    }

    private boolean isIgnoreUrl(String uri) {
        return securityProps.getIgnoreUrls().stream()
                .anyMatch(pattern -> matcher.match(pattern, uri));
    }
}