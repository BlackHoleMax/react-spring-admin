package dev.illichitcat.common.common.constant;

/**
 * JWT认证相关常量类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public final class JwtConstants {

    /**
     * Token相关常量
     */
    public static final String BEARER_TOKEN_TYPE = "Bearer ";
    public static final int TOKEN_EXTRACT_POSITION = 7;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PARAM_NAME = "token";
    public static final String TOKEN_COOKIE_NAME = "token";
    public static final String UNKNOWN_USER = "未知";

    /**
     * Redis相关常量
     */
    public static final String USER_TOKEN_REDIS_PREFIX = "user_token:";
    public static final String SESSION_TOKEN_REDIS_PREFIX = "session_token:";

    private JwtConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}