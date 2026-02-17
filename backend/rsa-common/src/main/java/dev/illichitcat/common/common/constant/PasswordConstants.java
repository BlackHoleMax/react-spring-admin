package dev.illichitcat.common.common.constant;

/**
 * 密码生成相关常量类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public final class PasswordConstants {

    /**
     * 密码长度相关常量
     */
    public static final int MIN_PASSWORD_LENGTH = 4;
    public static final int RECOMMENDED_PASSWORD_LENGTH = 8;

    /**
     * 字符类型标识常量
     */
    public static final int LOWERCASE_TYPE = 0;
    public static final int UPPERCASE_TYPE = 1;
    public static final int DIGIT_TYPE = 2;
    public static final int SPECIAL_TYPE = 3;

    private PasswordConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}