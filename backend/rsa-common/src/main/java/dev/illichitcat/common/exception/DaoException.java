package dev.illichitcat.common.exception;

import lombok.Getter;

/**
 * DAO层异常
 * 根据阿里巴巴开发规范,DAO层需要捕获所有异常并抛出自定义DAOException
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Getter
public class DaoException extends RuntimeException {

    private final int errorCode;

    public DaoException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DaoException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DaoException(String message) {
        super(message);
        this.errorCode = 500;
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 500;
    }
}
