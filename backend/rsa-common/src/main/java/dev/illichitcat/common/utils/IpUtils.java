package dev.illichitcat.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
public class IpUtils {
    private static final String DELIMITER = ",";

    /**
     * 常用代理IP请求头（按优先级排序）
     */
    private static final List<String> IP_HEADERS = Arrays.asList(
            // 最常用（多级代理时第一个为真实IP）
            "X-Forwarded-For",
            // Apache代理
            "Proxy-Client-IP",
            // WebLogic代理
            "WL-Proxy-Client-IP",
            // 部分代理
            "HTTP_CLIENT_IP",
            // 旧版代理
            "HTTP_X_FORWARDED_FOR",
            // Cloudflare
            "CF-Connecting-IP",
            // Nginx默认推荐（单级代理）
            "X-Real-IP",
            // 部分自定义代理
            "X-Proxy-ID"
    );

    /**
     * 内网IP段（排除本地/内网IP，仅在需获取公网IP时使用）
     */
    private static final List<String> INTERNAL_IP_PREFIXES = Arrays.asList(
            "0:", "10.", "127.", "172.16.", "172.17.", "172.18.", "172.19.",
            "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.",
            "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31.",
            "192.168.", "fe80:"
    );

    /**
     * 获取客户端真实IP（支持代理场景）
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        // 1. 从代理请求头中获取IP
        for (String header : IP_HEADERS) {
            ip = request.getHeader(header);
            // 跳过无效IP（unknown/空值）
            if (isValidIp(ip)) {
                break;
            }
        }

        // 2. 若代理头无有效IP，使用 request.getRemoteAddr()（可能是代理服务器IP）
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 3. 处理多级代理（X-Forwarded-For可能返回多个IP，取第一个有效IP）
        if (ip != null && ip.contains(DELIMITER)) {
            // 分割后取第一个IP
            ip = ip.split(",")[0].trim();
        }

        // 4. 排除内网IP（可选：若需获取公网IP，过滤内网地址）
        if (isInternalIp(ip)) {
            // 或返回空，根据业务需求调整
            ip = "0.0.0.0";
        }

        return ip;
    }

    /**
     * 判断IP是否有效（非unknown/空值/loopback）
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip)
                && !"unknown".equalsIgnoreCase(ip.trim())
                && !"127.0.0.1".equals(ip.trim())
                && !"localhost".equals(ip.trim());
    }

    /**
     * 判断是否为内网IP（IPv4/IPv6）
     */
    private static boolean isInternalIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return true;
        }
        ip = ip.trim().toLowerCase();
        for (String prefix : INTERNAL_IP_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}