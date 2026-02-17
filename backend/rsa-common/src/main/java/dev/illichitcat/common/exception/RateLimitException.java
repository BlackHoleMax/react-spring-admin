package dev.illichitcat.common.exception;

import dev.illichitcat.common.common.constant.ExceptionCodes;

/**
 * 限流异常
 * 当API接口请求超过限流阈值时抛出此异常
 *
 * @author Illichitcat
 * @since 2026/01/13
 */
public class RateLimitException extends BizException {

    private static final int RATE_LIMIT_ERROR = ExceptionCodes.RATE_LIMIT_ERROR;

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public RateLimitException(String message) {
        super(RATE_LIMIT_ERROR, message);
    }
}