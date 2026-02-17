package dev.illichitcat.common.utils;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import dev.illichitcat.common.common.constant.UserAgentConstants;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 * 用户代理工具类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
public class UserAgentUtils {

    private static Ip2Region ip2Region;
    private static Searcher searcher;

    static {
        try {
            ClassPathResource resource = new ClassPathResource(UserAgentConstants.Ip2RegionConstants.IP2REGION_DB_PATH);
            File dbFile = resource.getFile();
            String dbPath = dbFile.getPath();

            // 验证 xdb 文件
            try {
                Searcher.verifyFromFile(dbPath);
                log.info("ip2region xdb 文件验证通过");
            } catch (Exception e) {
                log.error("ip2region xdb 文件验证失败: {}", e.getMessage());
            }

            // 创建 v4 配置
            Config v4Config = Config.custom()
                    // 指定缓存策略
                    .setCachePolicy(Config.VIndexCache)
                    // 设置初始化的查询器数量
                    .setSearchers(UserAgentConstants.Ip2RegionConstants.DEFAULT_SEARCHER_COUNT)
                    // 设置 v4 xdb File 对象
                    .setXdbFile(dbFile)
                    // 指定为 v4 配置
                    .asV4();

            // 创建 Ip2Region 查询服务（只配置 v4，v6 为 null）
            ip2Region = Ip2Region.create(v4Config, null);

            // 同时创建一个 Searcher 备用方案
            searcher = Searcher.newWithFileOnly(Version.IPv4, dbPath);

            log.info("ip2region 初始化成功");
        } catch (Exception e) {
            log.error("ip2region 初始化失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取浏览器类型
     *
     * @param userAgentString 用户代理字符串
     * @return 浏览器类型
     */
    public static String getBrowser(String userAgentString) {
        try {
            UserAgent userAgent = UserAgentUtil.parse(userAgentString);
            return userAgent.getBrowser().getName();
        } catch (Exception e) {
            log.warn("解析浏览器类型失败: {}", userAgentString, e);
            return UserAgentConstants.IpConstants.UNKNOWN_BROWSER;
        }
    }

    /**
     * 获取操作系统
     *
     * @param userAgentString 用户代理字符串
     * @return 操作系统
     */
    public static String getOs(String userAgentString) {
        try {
            UserAgent userAgent = UserAgentUtil.parse(userAgentString);
            return userAgent.getOs().getName();
        } catch (Exception e) {
            log.warn("解析操作系统失败: {}", userAgentString, e);
            return UserAgentConstants.IpConstants.UNKNOWN_OS;
        }
    }

    /**
     * 根据IP获取地址
     *
     * @param ip IP地址
     * @return 地址
     */
    public static String getLocationByIp(String ip) {
        // 处理内网IP
        if (UserAgentConstants.IpConstants.LOCALHOST_IP.equals(ip) || UserAgentConstants.IpConstants.LOCALHOST_HOSTNAME.equals(ip) ||
                ip.startsWith(UserAgentConstants.IpConstants.PRIVATE_IP_192_PREFIX) || ip.startsWith(UserAgentConstants.IpConstants.PRIVATE_IP_10_PREFIX) ||
                isPrivate172Ip(ip)) {
            return UserAgentConstants.IpConstants.INTERNAL_NETWORK_IP;
        }

        String region = null;

        // 优先使用 Ip2Region 服务
        if (ip2Region != null) {
            try {
                region = ip2Region.search(ip);
                if (region != null && !region.isEmpty()) {
                    return parseRegion(region);
                }
            } catch (Exception e) {
                log.warn("使用 Ip2Region 查询IP地址失败: {}", ip, e);
            }
        }

        // 备用方案：使用 Searcher
        if (searcher != null) {
            try {
                region = searcher.search(ip);
                if (region != null && !region.isEmpty()) {
                    return parseRegion(region);
                }
            } catch (Exception e) {
                log.warn("使用 Searcher 查询IP地址失败: {}", ip, e);
            }
        }

        log.warn("无法查询IP地址: {}", ip);
        return UserAgentConstants.IpConstants.UNKNOWN_LOCATION;
    }

    /**
     * 判断是否为172.16.0.0-172.31.255.255范围内的私有IP
     *
     * @param ip IP地址
     * @return 是否是私有IP
     */
    private static boolean isPrivate172Ip(String ip) {
        if (!ip.startsWith(UserAgentConstants.IpConstants.PRIVATE_IP_172_PREFIX)) {
            return false;
        }

        try {
            String[] parts = ip.split("\\.");
            if (parts.length != UserAgentConstants.IpConstants.IP_PARTS_COUNT) {
                return false;
            }

            int secondOctet = Integer.parseInt(parts[1]);
            return secondOctet >= UserAgentConstants.IpConstants.PRIVATE_IP_172_START && secondOctet <= UserAgentConstants.IpConstants.PRIVATE_IP_172_END;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 解析地区信息
     *
     * @param region 原始地区信息，格式：国家|区域|省份|城市|ISP
     * @return 格式化后的地区信息
     */
    private static String parseRegion(String region) {
        if (region == null || region.isEmpty()) {
            return UserAgentConstants.IpConstants.UNKNOWN_LOCATION;
        }

        // 检查是否是内网IP的查询结果
        if (region.contains(UserAgentConstants.RegionConstants.INTERNAL_NETWORK_KEYWORD) ||
                region.contains(UserAgentConstants.RegionConstants.LAN_KEYWORD) ||
                region.contains(UserAgentConstants.RegionConstants.RESERVED_ADDRESS_KEYWORD)) {
            return UserAgentConstants.IpConstants.INTERNAL_NETWORK_IP;
        }

        // 解析结果格式：国家|区域|省份|城市|ISP
        String[] parts = region.split(UserAgentConstants.RegionConstants.REGION_SEPARATOR);
        if (parts.length >= UserAgentConstants.RegionConstants.REGION_PARTS_MIN_LENGTH) {
            // 组合省市信息
            String country = parts[UserAgentConstants.RegionConstants.REGION_COUNTRY_INDEX];
            String province = parts[UserAgentConstants.RegionConstants.REGION_PROVINCE_INDEX];
            String city = parts[UserAgentConstants.RegionConstants.REGION_CITY_INDEX];
            String isp = parts.length > UserAgentConstants.RegionConstants.REGION_ISP_INDEX ?
                    parts[UserAgentConstants.RegionConstants.REGION_ISP_INDEX] : "";

            // 构建友好的地址显示
            StringBuilder location = new StringBuilder();
            if (country != null && !UserAgentConstants.RegionConstants.INVALID_REGION_VALUE.equals(country) &&
                    !UserAgentConstants.RegionConstants.DEFAULT_COUNTRY.equals(country)) {
                location.append(country).append(" ");
            }
            if (province != null && !UserAgentConstants.RegionConstants.INVALID_REGION_VALUE.equals(province)) {
                location.append(province);
            }
            if (city != null && !UserAgentConstants.RegionConstants.INVALID_REGION_VALUE.equals(city)) {
                if (!location.isEmpty()) {
                    location.append(" ");
                }
                location.append(city);
            }
            if (isp != null && !UserAgentConstants.RegionConstants.INVALID_REGION_VALUE.equals(isp)) {
                location.append(" ").append(isp);
            }

            String result = location.toString();
            if (result.contains(UserAgentConstants.RegionConstants.INTERNAL_NETWORK_KEYWORD) &&
                    result.split(UserAgentConstants.RegionConstants.INTERNAL_NETWORK_KEYWORD).length > 1) {
                return UserAgentConstants.IpConstants.INTERNAL_NETWORK_IP;
            }

            return !result.isEmpty() ? result : region;
        }

        return region;
    }
}