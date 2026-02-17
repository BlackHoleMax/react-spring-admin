package dev.illichitcat.common.utils;

import dev.illichitcat.common.common.constant.PasswordConstants;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
public class SecurePasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALCHARS = "!@#$%^&*()\\-+=<>?/{}~|";

    private final Random random = new SecureRandom();

    /**
     * 生成随机密码
     *
     * @param length           密码长度（建议≥8）
     * @param includeUppercase 是否包含大写字母
     * @param includeDigits    是否包含数字
     * @param includeSpecial   是否包含特殊字符
     * @return 生成的密码
     */
    public String generate(int length, boolean includeUppercase, boolean includeDigits, boolean includeSpecial) {
        if (length < PasswordConstants.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("密码长度至少为" + PasswordConstants.MIN_PASSWORD_LENGTH + "位");
        }

        // 构建字符集
        StringBuilder charPool = new StringBuilder(LOWERCASE);
        if (includeUppercase) {
            charPool.append(UPPERCASE);
        }
        if (includeDigits) {
            charPool.append(DIGITS);
        }
        if (includeSpecial) {
            charPool.append(SPECIALCHARS);
        }

        char[] password = new char[length];
        // 确保至少包含每种类型的字符（避免纯小写/纯数字等弱密码）
        List<Integer> requiredTypes = new ArrayList<>();
        if (includeUppercase) {
            requiredTypes.add(PasswordConstants.UPPERCASE_TYPE);
        }
        if (includeDigits) {
            requiredTypes.add(PasswordConstants.DIGIT_TYPE);
        }
        if (includeSpecial) {
            requiredTypes.add(PasswordConstants.SPECIAL_TYPE);
        }

        // 填充必须包含的字符类型
        for (int i = 0; i < requiredTypes.size() && i < length; i++) {
            int type = requiredTypes.get(i);
            switch (type) {
                case PasswordConstants.UPPERCASE_TYPE:
                    // 大写字母
                    password[i] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
                    break;
                case PasswordConstants.DIGIT_TYPE:
                    // 数字
                    password[i] = DIGITS.charAt(random.nextInt(DIGITS.length()));
                    break;
                case PasswordConstants.SPECIAL_TYPE:
                    // 特殊字符
                    password[i] = SPECIALCHARS.charAt(random.nextInt(SPECIALCHARS.length()));
                    break;
                default:
                    password[i] = DIGITS.charAt(random.nextInt(DIGITS.length()));
                    break;
            }
        }

        // 填充剩余字符（随机从字符集选取）
        for (int i = requiredTypes.size(); i < length; i++) {
            password[i] = charPool.charAt(random.nextInt(charPool.length()));
        }

        // 打乱顺序（避免前几位固定为必须类型）
        shuffleArray(password);

        return new String(password);
    }

    /**
     * 打乱字符数组顺序（Fisher-Yates洗牌算法）
     */
    private void shuffleArray(char[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}