package dev.illichitcat.system.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知公告DTO类
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@Schema(description = "通知公告DTO")
public class NoticeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题长度不能超过200")
    @Schema(description = "公告标题")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容")
    private String content;

    @NotNull(message = "公告类型不能为空")
    @Min(value = 1, message = "公告类型值必须为1或2")
    @Max(value = 2, message = "公告类型值必须为1或2")
    @Schema(description = "公告类型 1=系统公告 2=活动通知")
    private Integer type;

    @NotNull(message = "发布范围不能为空")
    @Min(value = 1, message = "发布范围值必须为1或2")
    @Max(value = 2, message = "发布范围值必须为1或2")
    @Schema(description = "发布范围 1=全部用户 2=指定角色")
    private Integer targetType;

    @Schema(description = "指定角色ID列表")
    private List<Long> targetRoles;

    @NotNull(message = "优先级不能为空")
    @Min(value = 1, message = "优先级值必须为1-3")
    @Max(value = 3, message = "优先级值必须为1-3")
    @Schema(description = "优先级 1=普通 2=重要 3=紧急")
    private Integer priority;

    @Schema(description = "生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}