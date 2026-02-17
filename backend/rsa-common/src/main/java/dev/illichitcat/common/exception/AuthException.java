package dev.illichitcat.common.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Getter
public class AuthException extends AuthenticationException {

    private final int errorCode;

    public AuthException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}