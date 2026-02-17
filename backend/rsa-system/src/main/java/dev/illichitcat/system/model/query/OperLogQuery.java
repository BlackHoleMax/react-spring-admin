package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志查询参数
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志查询参数")
public class OperLogQuery extends PageQuery {

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "操作人员")
    private String operName;

    @Schema(description = "操作状态")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
