package dev.illichitcat.common.exception;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import lombok.Getter;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Getter
public class BizException extends RuntimeException {
    private final int errorCode;

    public BizException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(String message) {
        this(ExceptionCodes.BIZ_ERROR, message);
    }
}