package dev.illichitcat.api;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.common.utils.JwtUtil;
import dev.illichitcat.common.utils.UserAgentUtils;
import dev.illichitcat.system.config.RateLimit;
import dev.illichitcat.system.model.entity.LoginLog;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.model.entity.UserOnline;
import dev.illichitcat.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dev.illichitcat.common.utils.IpUtils.getClientIp;

/**
 * 认证授权控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "认证授权", description = "提供用户登录、注销等认证授权功能")
@RestController
@Slf4j
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserOnlineService userOnlineService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 记录登录日志
     *
     * @param username 用户名
     * @param userId   用户ID
     * @param status   登录状态 1成功 0失败
     * @param msg      登录信息
     * @param request  HTTP请求对象
     */
    private void recordLoginLog(String username, Long userId, Integer status, String msg, HttpServletRequest request) {
        try {
            LoginLog loginLog = new LoginLog();
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setStatus(status);
            loginLog.setMsg(msg);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setIp(getClientIp(request));
            loginLog.setUserAgent(request.getHeader("User-Agent"));
            loginLogService.insertLoginLogAsync(loginLog);
        } catch (Exception e) {
            // 记录日志失败不影响登录流程
            System.err.println("发送登录日志失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @Operation(
            summary = "用户登录",
            description = "用户登录并获取JWT令牌"
    )
    @ApiResponse(
            responseCode = "200",
            description = "登录成功",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class)
            )
    )
    @RateLimit(key = "login:", time = 60, count = 5, limitType = RateLimit.LimitType.IP)
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // 检查是否开启验证码
        boolean captchaEnabled = configService.getBooleanValue("captcha.login.enabled", true);

        // 如果开启了验证码但没有验证码验证信息，则要求验证码
        if (captchaEnabled && loginRequest.getCaptchaVerification() == null) {
            recordLoginLog(loginRequest.getUsername(), null, 0, "未进行验证码验证", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "请先完成验证码验证");
        }

        // 如果有验证码验证成功的响应，直接使用验证码中的用户信息进行登录
        if (loginRequest.getCaptchaVerification() != null) {
            return handleCaptchaLogin(loginRequest, request);
        }

        // 原有的用户名密码登录逻辑
        return handlePasswordLogin(loginRequest, request);
    }

    /**
     * 处理验证码登录
     */
    private Result<LoginResponse> handleCaptchaLogin(LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        if (username == null || username.isEmpty()) {
            recordLoginLog(null, null, 0, "验证码验证成功但缺少用户名", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "验证码验证成功但缺少用户名");
        }

        User user = validateUser(username, request);

        // 验证总在线人数是否超过上限（单点登录已在 addOnlineUser 中自动处理）
        validateOnlineUserCount(user, request);

        recordLoginLog(username, user.getId(), 1, "验证码登录成功", request);

        String token = generateToken(user);
        List<String> permissions = permissionService.selectPermsByUserId(user.getId());
        storeUserPermissions(user.getId(), permissions);
        storeUserToken(user.getId(), token);

        String sessionId = addOnlineUser(user, request);
        storeSessionTokenMapping(sessionId, token);

        // 处理记住我功能
        String rememberMeToken = null;
        if (Boolean.TRUE.equals(loginRequest.getRememberMe())) {
            rememberMeToken = generateAndStoreRememberMeToken(user.getId(), user.getUsername());
        }

        return buildLoginResponse(user, token, permissions, rememberMeToken);
    }

    /**
     * 处理密码登录
     */
    private Result<LoginResponse> handlePasswordLogin(LoginRequest loginRequest, HttpServletRequest request) {
        validateLoginInput(loginRequest, request);

        User user = userService.selectUserByUsername(loginRequest.getUsername());
        if (user == null) {
            recordLoginLog(loginRequest.getUsername(), null, 0, "用户名或密码错误", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户名或密码错误");
        }

        validateUserStatus(user, request);
        validatePassword(loginRequest.getPassword(), user, request);

        // 验证总在线人数是否超过上限（单点登录已在 addOnlineUser 中自动处理）
        validateOnlineUserCount(user, request);

        recordLoginLog(loginRequest.getUsername(), user.getId(), 1, "登录成功", request);

        String token = generateToken(user);
        List<String> permissions = permissionService.selectPermsByUserId(user.getId());
        storeUserPermissions(user.getId(), permissions);
        storeUserToken(user.getId(), token);
        addOnlineUser(user, request);

        // 处理记住我功能
        String rememberMeToken = null;
        if (Boolean.TRUE.equals(loginRequest.getRememberMe())) {
            rememberMeToken = generateAndStoreRememberMeToken(user.getId(), user.getUsername());
        }

        return buildLoginResponse(user, token, permissions, rememberMeToken);
    }

    /**
     * 验证用户输入
     */
    private void validateLoginInput(LoginRequest loginRequest, HttpServletRequest request) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            recordLoginLog(loginRequest.getUsername(), null, 0, "用户名或密码不能为空", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户名或密码不能为空");
        }
    }

    /**
     * 验证用户状态
     */
    private User validateUser(String username, HttpServletRequest request) {
        User user = userService.selectUserByUsername(username);
        if (user == null) {
            recordLoginLog(username, null, 0, "用户不存在", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户不存在");
        }

        if (user.getStatus() == 0) {
            recordLoginLog(username, user.getId(), 0, "用户已被禁用", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户已被禁用");
        }

        return user;
    }

    /**
     * 验证用户状态
     */
    private void validateUserStatus(User user, HttpServletRequest request) {
        if (user.getStatus() == 0) {
            recordLoginLog(user.getUsername(), user.getId(), 0, "用户已被禁用", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户已被禁用");
        }
    }

    /**
     * 验证密码
     */
    private void validatePassword(String password, User user, HttpServletRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            recordLoginLog(user.getUsername(), user.getId(), 0, "用户名或密码错误", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "用户名或密码错误");
        }
    }

    /**
     * 验证总在线人数是否超过上限
     * 注意：单点登录逻辑已在 addOnlineUser 方法中自动处理（自动踢出旧会话）
     *
     * @param user    用户信息
     * @param request HTTP请求对象
     */
    private void validateOnlineUserCount(User user, HttpServletRequest request) {
        // 获取总用户数（使用缓存优化）
        long totalUserCount = userService.getTotalUserCount();

        // 验证总在线人数是否超过总用户数
        if (userOnlineService.isOnlineCountExceedsTotal(totalUserCount)) {
            recordLoginLog(user.getUsername(), user.getId(), 0, "在线人数已达到上限", request);
            throw new BizException(ExceptionCodes.UNAUTHORIZED, "在线人数已达到上限，请稍后再试");
        }
    }

    /**
     * 生成JWT令牌
     */
    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        return jwtUtil.generateToken(claims);
    }

    /**
     * 存储用户权限到Redis
     */
    private void storeUserPermissions(Long userId, List<String> permissions) {
        String permKey = "user_perms:" + userId;
        redisTemplate.delete(permKey);
        if (!permissions.isEmpty()) {
            redisTemplate.opsForSet().add(permKey, permissions.toArray(new String[0]));
            redisTemplate.expire(permKey, 24, TimeUnit.HOURS);
        }
    }

    /**
     * 存储用户token到Redis
     */
    private void storeUserToken(Long userId, String token) {
        String tokenKey = "user_token:" + userId;
        redisTemplate.opsForValue().set(tokenKey, token, 24, TimeUnit.HOURS);
    }

    /**
     * 存储会话ID与token的映射关系
     */
    private void storeSessionTokenMapping(String sessionId, String token) {
        if (sessionId != null) {
            redisTemplate.opsForValue().set("session_token:" + sessionId, token, 24, TimeUnit.HOURS);
        }
    }

    /**
     * 添加在线用户记录
     */
    private String addOnlineUser(User user, HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String ip = getClientIp(request);
            String sessionId = java.util.UUID.randomUUID().toString().replace("-", "");

            UserOnline userOnline = new UserOnline();
            userOnline.setId(sessionId);
            userOnline.setUserId(user.getId());
            userOnline.setUsername(user.getUsername());
            userOnline.setNickname(user.getNickname());
            userOnline.setIp(ip);
            userOnline.setLocation(UserAgentUtils.getLocationByIp(ip));
            userOnline.setBrowser(UserAgentUtils.getBrowser(userAgent));
            userOnline.setOs(UserAgentUtils.getOs(userAgent));
            userOnline.setStatus("online");
            userOnline.setStartTime(LocalDateTime.now());
            userOnline.setLastTime(LocalDateTime.now());
            userOnline.setExpireTime(LocalDateTime.now().plusHours(24));

            userOnlineService.addOnlineUser(userOnline);
            return sessionId;
        } catch (Exception e) {
            System.err.println("添加在线用户记录失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 生成并存储记住我 token
     */
    private String generateAndStoreRememberMeToken(Long userId, String username) {
        // 生成唯一的记住我 token
        String rememberMeToken = java.util.UUID.randomUUID().toString().replace("-", "");

        // 存储到 Redis，有效期 30 天
        String rememberKey = "remember_me:" + rememberMeToken;
        String rememberValue = userId + ":" + username;
        redisTemplate.opsForValue().set(rememberKey, rememberValue, 30, TimeUnit.DAYS);

        return rememberMeToken;
    }

    /**
     * 构造登录响应结果
     */
    private Result<LoginResponse> buildLoginResponse(User user, String token, List<String> permissions, String rememberMeToken) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPermissions(permissions);
        response.setRememberMeToken(rememberMeToken);
        return Result.ok(response);
    }

    /**
     * 用户注销
     *
     * @return 注销结果
     */
    @Operation(
            summary = "用户注销",
            description = "用户注销并清除JWT令牌"
    )
    @ApiResponse(
            responseCode = "200",
            description = "注销成功"
    )
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestAttribute(required = false) Long userId,
                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (userId != null) {
            // 清除Redis中的token和权限
            redisTemplate.delete("user_token:" + userId);
            redisTemplate.delete("user_perms:" + userId);

            // 删除在线用户记录
            String tokenPrefix = "Bearer ";
            try {
                if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
                    String token = authHeader.substring(7);

                    // 通过token查找对应的sessionId
                    String sessionId = null;
                    java.util.Set<String> sessionKeys = redisTemplate.keys("session_token:*");
                    for (String key : sessionKeys) {
                        String storedToken = redisTemplate.opsForValue().get(key);
                        if (storedToken != null && storedToken.equals(token)) {
                            sessionId = key.substring("session_token:".length());
                            // 删除会话映射
                            redisTemplate.delete(key);
                            break;
                        }
                    }

                    if (sessionId != null) {
                        userOnlineService.kickoutBySessionId(sessionId);
                    }
                } else {
                    // 如果没有token，根据用户ID删除所有会话
                    userOnlineService.kickoutByUserId(userId);
                }
            } catch (Exception e) {
                // 删除在线用户记录失败不影响注销流程
                log.error("删除在线用户记录失败", e);
            }
        }
        return Result.ok();
    }

    /**
     * 登录请求参数
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    @Schema(description = "登录请求参数")
    public static class LoginRequest {
        @Schema(description = "用户名")
        private String username;

        @Schema(description = "密码")
        private String password;

        @Schema(description = "验证码验证响应")
        private Object captchaVerification;

        @Schema(description = "记住我")
        private Boolean rememberMe;
    }

    /**
     * 登录响应结果
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    @Schema(description = "登录响应结果")
    public static class LoginResponse {
        @Schema(description = "JWT令牌")
        private String token;

        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "权限列表")
        private List<String> permissions;

        @Schema(description = "记住我令牌（用于自动登录）")
        private String rememberMeToken;
    }
}