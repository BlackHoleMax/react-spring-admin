package dev.illichitcat.system.config;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.constant.JwtConstants;
import dev.illichitcat.common.common.properties.SecurityProperties;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.common.utils.JwtUtil;
import dev.illichitcat.system.service.UserOnlineService;
import dev.illichitcat.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;

/**
 * JWT认证拦截器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final SecurityProperties securityProps;
    private final UserOnlineService userOnlineService;
    private final AntPathMatcher matcher = new AntPathMatcher();
    // 移除重复的常量，使用UserAgentConstants中的常量

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String uri = request.getRequestURI();

        /* 1. 白名单直接放行 */
        if (isIgnoreUrl(uri)) {
            return true;
        }

        /* 2. 提取并前置校验 Bearer Token */
        String token = resolveToken(request);
        if (token == null) {
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "Token 缺失");
        }

        /* 3. 统一 JWT 校验（含过期、签名、Redis 一致性） */
        if (!jwtUtil.validateToken(token)) {
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "Token 无效");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userService.selectUserById(userId) == null) {
            throw new BizException(ExceptionCodes.USER_NOT_FOUND, "用户不存在");
        }

        /* 4. 单点登录一致性 */
        String redisToken = redisTemplate.opsForValue().get(JwtConstants.USER_TOKEN_REDIS_PREFIX + userId);
        if (redisToken == null) {
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "认证已失效，请重新登录");
        }
        if (!redisToken.equals(token)) {
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "认证已失效，请重新登录");
        }

        /* 5. 更新在线用户最后访问时间 */
        try {
            // 通过token查找对应的sessionId
            String sessionId = null;
            java.util.Set<String> sessionKeys = redisTemplate.keys(JwtConstants.SESSION_TOKEN_REDIS_PREFIX + "*");
            for (String key : sessionKeys) {
                String storedToken = redisTemplate.opsForValue().get(key);
                if (storedToken != null && storedToken.equals(token)) {
                    sessionId = key.substring(JwtConstants.SESSION_TOKEN_REDIS_PREFIX.length());
                    break;
                }
            }

            if (sessionId != null) {
                userOnlineService.updateLastAccessTime(sessionId);
            }
        } catch (Exception e) {
            // 更新失败不影响正常流程
            log.error("更新在线用户最后访问时间失败", e);
        }

        /* 6. 注入上下文（供后续业务使用） */
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        return true;
    }

    /**
     * ---------- 工具 ----------
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer != null && bearer.startsWith(JwtConstants.BEARER_TOKEN_TYPE)) {
            return bearer.substring(JwtConstants.TOKEN_EXTRACT_POSITION);
        }

        String tokenParam = request.getParameter(JwtConstants.TOKEN_PARAM_NAME);
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if (JwtConstants.TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private boolean isIgnoreUrl(String uri) {
        return securityProps.getIgnoreUrls().stream()
                .anyMatch(pattern -> matcher.match(pattern, uri));
    }
}