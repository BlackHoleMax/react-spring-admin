package dev.illichitcat.system.config;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiDocInterceptor implements HandlerInterceptor {

    private static final String DOC_PATH = "/doc.html";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        log.info("ApiDocInterceptor - URI: {}", uri);

        if (DOC_PATH.equals(uri)) {

            Long userId = (Long) request.getAttribute("userId");
            log.info("ApiDocInterceptor - userId: {}", userId);

            if (userId == null) {
                throw new AuthException(ExceptionCodes.UNAUTHORIZED, "用户未登录");
            }

            String key = "user_perms:" + userId;
            Boolean hasPermission = redisTemplate.opsForSet().isMember(key, "api-doc:view");
            log.info("ApiDocInterceptor - hasPermission: {}", hasPermission);

            if (Boolean.FALSE.equals(hasPermission)) {
                throw new AuthException(ExceptionCodes.FORBIDDEN, "无权限访问API文档");
            }
        }

        return true;
    }
}
