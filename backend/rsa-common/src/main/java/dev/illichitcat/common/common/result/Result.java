package dev.illichitcat.common.common.result;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用返回对象
 *
 * @param <T> 业务数据类型
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Accessors(chain = true)
@Schema(description = "统一响应模型")
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务码：0=成功，其余自定义
     */
    @Schema(description = "业务码", example = "0")
    private Integer code;

    /**
     * 消息
     */
    @Schema(description = "响应消息", example = "操作成功")
    private String msg;

    /**
     * 承载数据
     */
    @Schema(description = "业务数据")
    private T data;

    /**
     * 当前时间戳（毫秒）
     */
    @Schema(description = "响应时间戳")
    private Long timestamp;

    /**
     * 请求路径（可选）
     */
    @Schema(description = "请求路径")
    private String path;

    /**
     * 分页信息（可选）
     */
    @Schema(description = "分页信息")
    private PageInfo page;

    /**
     * 调试信息（开发环境开启）
     */
    @Schema(description = "调试信息")
    private Object debug;

    public static <T> Result<T> ok() {
        return ok(null);
    }

    /* ------------------ 静态工厂 ------------------ */

    public static <T> Result<T> ok(T data) {
        return new Result<T>()
                .setCode(ExceptionCodes.SUCCESS)
                .setMsg("操作成功")
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result<T> ok(T data, String msg) {
        return new Result<T>()
                .setCode(ExceptionCodes.SUCCESS)
                .setMsg(msg)
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<T>()
                .setCode(ExceptionCodes.SUCCESS)
                .setMsg(msg)
                .setData(null)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result<T> ok(T data, PageInfo page) {
        return ok(data).setPage(page);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<T>()
                .setCode(code)
                .setMsg(msg)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 链式扩展
     */
    public Result<T> path(String path) {
        this.path = path;
        return this;
    }

    public Result<T> debug(Object debug) {
        this.debug = debug;
        return this;
    }

    /**
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    @Accessors(chain = true)
    public static class PageInfo {
        private Long total;
        private Long size;
        private Long current;
    }
}