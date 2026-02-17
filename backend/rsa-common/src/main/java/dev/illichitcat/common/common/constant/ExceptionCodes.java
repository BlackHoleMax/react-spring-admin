package dev.illichitcat.common.common.constant;

/**
 * 异常码常量类
 * 根据阿里巴巴开发规范定义异常码
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public final class ExceptionCodes {

    /**
     * 通用异常码 1xxxx
     */
    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int PARAM_ERROR = 1001;
    public static final int NOT_FOUND = 1004;
    public static final int SYSTEM_ERROR = 1005;
    public static final int RATE_LIMIT_ERROR = 1006;

    /**
     * 业务异常码 2xxxx
     */
    public static final int BIZ_ERROR = 2001;
    public static final int USER_NOT_FOUND = 2002;

    /**
     * 数据异常码 3xxxx
     */
    public static final int DATA_DUPLICATE = 3001;
    public static final int DAO_ERROR = 3002;

    /**
     * 私有构造函数,防止实例化
     */
    private ExceptionCodes() {
        throw new AssertionError("工具类不允许实例化");
    }
}