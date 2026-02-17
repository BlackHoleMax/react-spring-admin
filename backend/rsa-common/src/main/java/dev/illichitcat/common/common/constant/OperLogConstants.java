package dev.illichitcat.common.common.constant;

/**
 * 操作日志相关常量类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public final class OperLogConstants {

    /**
     * 内容长度限制常量
     */
    public static final int MAX_CONTENT_LENGTH = 2000;
    public static final String TRUNCATE_SUFFIX = "...";

    /**
     * 操作状态常量
     */
    public static final int SUCCESS_STATUS = 0;
    public static final int ERROR_STATUS = 1;

    /**
     * 方法相关常量
     */
    public static final String METHOD_SUFFIX = "()";

    private OperLogConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}