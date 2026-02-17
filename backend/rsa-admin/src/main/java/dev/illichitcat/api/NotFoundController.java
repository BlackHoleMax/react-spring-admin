package dev.illichitcat.api;

import dev.illichitcat.common.common.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

/**
 * 兜底 404 处理器（优先级最低）
 * 仅当没有任何 Controller 映射 & 静态资源时才进入
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@RestController
@Hidden
public class NotFoundController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<Result<Void>> handle404(HttpServletRequest req) {
        // 可扩展：读取 spring.error.whitelabel 开关、记录日志等
        String requestUri = HtmlUtils.htmlEscape(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.fail(404, "资源不存在: " + requestUri));
    }
}