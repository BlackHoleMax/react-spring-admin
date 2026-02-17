package dev.illichitcat.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.illichitcat.common.common.constant.JwtConstants;
import dev.illichitcat.common.common.constant.OperLogConstants;
import dev.illichitcat.common.utils.IpUtils;
import dev.illichitcat.common.utils.JwtUtil;
import dev.illichitcat.system.model.entity.OperLog;
import dev.illichitcat.system.service.OperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    private static final ThreadLocal<OperLog> OPER_LOG = new ThreadLocal<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OperLogService operLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Before("@annotation(operationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog operationLog) {
        START_TIME.set(System.currentTimeMillis());

        OperLog log = new OperLog();
        log.setTitle(operationLog.title());
        log.setBusinessType(operationLog.businessType().getCode());
        log.setOperatorType(operationLog.operatorType().getCode());
        log.setOperTime(LocalDateTime.now());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setRequestMethod(request.getMethod());
            log.setOperUrl(request.getRequestURI());
            log.setOperIp(IpUtils.getClientIp(request));

            String token = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
            if (token != null && token.startsWith(JwtConstants.BEARER_TOKEN_TYPE)) {
                try {
                    String username = jwtUtil.getUsernameFromToken(token.substring(JwtConstants.TOKEN_EXTRACT_POSITION));
                    log.setOperName(username);
                } catch (Exception e) {
                    log.setOperName(JwtConstants.UNKNOWN_USER);
                }
            }
        }

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        log.setMethod(className + "." + methodName + OperLogConstants.METHOD_SUFFIX);

        if (operationLog.isSaveRequestData()) {
            try {
                Object[] args = joinPoint.getArgs();
                Map<String, Object> params = new HashMap<>(args.length);
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && !isFilterObject(args[i])) {
                        params.put("arg" + i, args[i]);
                    }
                }
                String jsonParams = objectMapper.writeValueAsString(params);
                if (jsonParams.length() > OperLogConstants.MAX_CONTENT_LENGTH) {
                    jsonParams = jsonParams.substring(0, OperLogConstants.MAX_CONTENT_LENGTH) + OperLogConstants.TRUNCATE_SUFFIX;
                }
                log.setOperParam(jsonParams);
            } catch (Exception e) {
                OperationLogAspect.log.error("记录操作日志参数异常", e);
            }
        }

        OPER_LOG.set(log);
    }

    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog operationLog, Object result) {
        handleLog(operationLog, result, null);
    }

    @AfterThrowing(pointcut = "@annotation(operationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception e) {
        handleLog(operationLog, null, e);
    }

    private void handleLog(OperationLog operationLog, Object result, Exception e) {
        try {
            OperLog log = OPER_LOG.get();
            if (log == null) {
                return;
            }

            Long startTime = START_TIME.get();
            if (startTime != null) {
                log.setCostTime(System.currentTimeMillis() - startTime);
            }

            if (e != null) {
                log.setStatus(OperLogConstants.ERROR_STATUS);
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.length() > OperLogConstants.MAX_CONTENT_LENGTH) {
                    errorMsg = errorMsg.substring(0, OperLogConstants.MAX_CONTENT_LENGTH);
                }
                log.setErrorMsg(errorMsg);
            } else {
                log.setStatus(OperLogConstants.SUCCESS_STATUS);
            }

            if (operationLog.isSaveResponseData() && result != null) {
                try {
                    String jsonResult = objectMapper.writeValueAsString(result);
                    if (jsonResult.length() > OperLogConstants.MAX_CONTENT_LENGTH) {
                        jsonResult = jsonResult.substring(0, OperLogConstants.MAX_CONTENT_LENGTH) + OperLogConstants.TRUNCATE_SUFFIX;
                    }
                    log.setJsonResult(jsonResult);
                } catch (Exception ex) {
                    OperationLogAspect.log.error("记录操作日志响应异常", ex);
                }
            }

            // 异步保存操作日志
            operLogService.insertOperLogAsync(log);
        } catch (Exception ex) {
            OperationLogAspect.log.error("记录操作日志异常", ex);
        } finally {
            OPER_LOG.remove();
            START_TIME.remove();
        }
    }

    private boolean isFilterObject(Object obj) {
        return obj instanceof HttpServletRequest
                || obj instanceof jakarta.servlet.http.HttpServletResponse
                || obj instanceof org.springframework.web.multipart.MultipartFile;
    }
}
