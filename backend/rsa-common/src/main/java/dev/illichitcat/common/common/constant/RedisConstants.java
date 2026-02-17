package dev.illichitcat.common.common.constant;

/**
 * Redis常量类
 *
 * @author Illichitcat
 * @since 2025/01/08
 */
public final class RedisConstants {

    /**
     * Redis数据类型
     */
    public static final String TYPE_STRING = "string";
    public static final String TYPE_HASH = "hash";
    public static final String TYPE_LIST = "list";
    public static final String TYPE_SET = "set";
    public static final String TYPE_ZSET = "zset";
    public static final String TYPE_NONE = "none";
    public static final String TYPE_UNKNOWN = "UNKNOWN";

    /**
     * Redis键模式
     */
    public static final String KEY_PATTERN_ALL = "*";

    /**
     * Redis命令统计前缀
     */
    public static final String CMDSTAT_PREFIX = "cmdstat_";

    /**
     * TTL值
     */
    public static final long TTL_NO_EXPIRE = -1L;
    public static final long TTL_KEY_NOT_EXIST = -2L;

    /**
     * 私有构造函数，防止实例化
     */
    private RedisConstants() {
        throw new AssertionError("常量类不允许实例化");
    }
}