package dev.illichitcat.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Component
public class JwtUtil {

    /**
     * JWT声明：用户ID
     */
    private static final String USERID = "userId";

    /**
     * JWT声明：用户名
     */
    private static final String USERNAME = "username";

    /**
     * JWT密钥（使用固定密钥，确保生成和验证时使用相同的密钥）
     */
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();
    /**
     * JWT过期时间（默认24小时）
     */
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * 生成JWT令牌
     *
     * @param claims 自定义声明
     * @return JWT令牌
     */
    public String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 从JWT令牌中解析声明
     *
     * @param token JWT令牌
     * @return 声明，如果解析失败返回 null
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID，如果解析失败返回 null
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims == null || claims.get(USERID) == null) {
                return null;
            }
            return Long.valueOf(claims.get(USERID).toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名，如果解析失败返回 null
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims == null || claims.get(USERNAME) == null) {
                return null;
            }
            return claims.get(USERNAME).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断JWT令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期，如果解析失败返回 true（视为无效token）
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims == null || claims.getExpiration() == null) {
                return true;
            }
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}