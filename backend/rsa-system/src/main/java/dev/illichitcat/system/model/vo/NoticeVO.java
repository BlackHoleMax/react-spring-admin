package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知公告视图对象
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@Schema(description = "通知公告视图对象")
public class NoticeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告类型 1=系统公告 2=活动通知")
    private Integer type;

    @Schema(description = "公告类型名称")
    private String typeName;

    @Schema(description = "发布范围 1=全部用户 2=指定角色")
    private Integer targetType;

    @Schema(description = "发布范围名称")
    private String targetTypeName;

    @Schema(description = "指定角色ID列表")
    private String targetRoles;

    @Schema(description = "优先级 1=普通 2=重要 3=紧急")
    private Integer priority;

    @Schema(description = "优先级名称")
    private String priorityName;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态 1=草稿 2=已发布 3=已撤回")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布者姓名")
    private String publisherName;

    @Schema(description = "已读人数")
    private Integer readCount;

    @Schema(description = "目标总人数")
    private Integer totalCount;

    @Schema(description = "阅读率")
    private String readRate;

    @Schema(description = "是否已读 0=未读 1=已读")
    private Integer readStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}