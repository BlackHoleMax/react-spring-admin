package dev.illichitcat.common.common.constant;

/**
 * UserAgent相关常量类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public final class UserAgentConstants {

    private UserAgentConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * IP地址相关常量
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    public static final class IpConstants {
        public static final String LOCALHOST_IP = "127.0.0.1";
        public static final String LOCALHOST_HOSTNAME = "localhost";
        public static final String PRIVATE_IP_192_PREFIX = "192.168.";
        public static final String PRIVATE_IP_10_PREFIX = "10.";
        public static final String PRIVATE_IP_172_PREFIX = "172.";

        // 172.16.0.0-172.31.255.255 私有IP范围
        public static final int PRIVATE_IP_172_START = 16;
        public static final int PRIVATE_IP_172_END = 31;

        public static final String INTERNAL_NETWORK_IP = "内网IP";
        public static final String UNKNOWN_LOCATION = "未知地点";
        public static final String UNKNOWN_BROWSER = "未知";
        public static final String UNKNOWN_OS = "未知";

        // IP地址部分数量（IPv4地址由4个部分组成）
        public static final int IP_PARTS_COUNT = 4;
    }

    /**
     * IP2Region配置相关常量
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    public static final class Ip2RegionConstants {
        public static final String IP2REGION_DB_PATH = "geoip/ip2region_v4.xdb";
        public static final int DEFAULT_SEARCHER_COUNT = 10;
        public static final int IPV4_VERSION = 4;
    }

    /**
     * 地区解析相关常量
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    public static final class RegionConstants {
        public static final String REGION_SEPARATOR = "\\|";
        public static final int REGION_PARTS_MIN_LENGTH = 4;
        public static final int REGION_COUNTRY_INDEX = 0;
        public static final int REGION_PROVINCE_INDEX = 2;
        public static final int REGION_CITY_INDEX = 3;
        public static final int REGION_ISP_INDEX = 4;
        public static final String INVALID_REGION_VALUE = "0";
        public static final String DEFAULT_COUNTRY = "中国";

        public static final String INTERNAL_NETWORK_KEYWORD = "内网IP";
        public static final String LAN_KEYWORD = "局域网";
        public static final String RESERVED_ADDRESS_KEYWORD = "保留地址";
    }
}