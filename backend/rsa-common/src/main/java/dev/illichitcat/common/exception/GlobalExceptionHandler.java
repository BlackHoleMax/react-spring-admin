package dev.illichitcat.common.exception;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 1. 业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e, HttpServletRequest req) {
        logger.warn("[BizException] uri={}, message={}, code={}",
                req.getRequestURI(), e.getMessage(), e.getErrorCode());
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 1.1 DAO层异常
     */
    @ExceptionHandler(DaoException.class)
    public Result<Void> handleDao(DaoException e, HttpServletRequest req) {
        logger.error("[DaoException] uri={}, message={}, code={}",
                req.getRequestURI(), e.getMessage(), e.getErrorCode(), e);
        return Result.fail(e.getErrorCode(), "数据访问异常");
    }

    /**
     * 1.2 限流异常
     */
    @ExceptionHandler(RateLimitException.class)
    public Result<Void> handleRateLimit(RateLimitException e, HttpServletRequest req) {
        logger.warn("[RateLimitException] uri={}, message={}, code={}",
                req.getRequestURI(), e.getMessage(), e.getErrorCode());
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 2. 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return Result.fail(ExceptionCodes.PARAM_ERROR, msg);
    }

    /**
     * 3. Spring Security 认证/授权
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuth(AuthenticationException e) {
        return Result.fail(ExceptionCodes.UNAUTHORIZED, "未认证");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleDenied(AccessDeniedException e) {
        return Result.fail(ExceptionCodes.FORBIDDEN, "无权限");
    }

    /**
     * 4. 数据库唯一键冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicate(DuplicateKeyException e) {
        logger.error("[Duplicate] {}", e.getMessage());
        return Result.fail(ExceptionCodes.DATA_DUPLICATE, "数据已存在");
    }

    /**
     * 5. 404 静态资源兜底（可选）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handle404(NoResourceFoundException e, HttpServletRequest req) {
        logger.warn("[404] {}", req.getRequestURI());
        return Result.fail(ExceptionCodes.NOT_FOUND, "资源不存在");
    }

    /**
     * 6. 客户端主动断开连接（如用户刷新页面、关闭标签页等）
     */
    @ExceptionHandler(org.apache.catalina.connector.ClientAbortException.class)
    public Result<Void> handleClientAbort(org.apache.catalina.connector.ClientAbortException e, HttpServletRequest req) {
        logger.warn("[ClientAbort] uri={}, client disconnected", req.getRequestURI());
        return Result.fail(ExceptionCodes.SYSTEM_ERROR, "客户端断开连接");
    }

    /**
     * 7. 兜底 500
     */
    @ExceptionHandler(Throwable.class)
    public Result<Void> handleAll(Throwable e, HttpServletRequest req) {
        logger.error("[500] path={}", req.getRequestURI(), e);
        return Result.fail(ExceptionCodes.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }
}