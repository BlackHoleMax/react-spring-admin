package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Redis命令统计视图对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "Redis命令统计视图对象")
public class CommandStatVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "命令名称")
    private String name;

    @Schema(description = "调用次数")
    private Long calls;

    @Schema(description = "总耗时(微秒)")
    private Long usec;
}