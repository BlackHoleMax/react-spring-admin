package dev.illichitcat.generator.utils;

/**
 * 字符串工具类
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 转换为驼峰命名
     *
     * @param str 字符串
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                upperCase = true;
            } else {
                if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        // 首字母大写
        if (!sb.isEmpty()) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否包含指定字符串
     *
     * @param str       字符串
     * @param searchStr 要查找的字符串
     * @return 是否包含
     */
    public static boolean indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    /**
     * 截取字符串中指定字符之前的内容
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 截取后的字符串
     */
    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取字符串中指定字符之后的内容
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 截取后的字符串
     */
    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return "";
        }
        if (separator == null) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
}