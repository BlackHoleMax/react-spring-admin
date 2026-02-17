package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 我的通知视图对象
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@Schema(description = "我的通知视图对象")
public class MyNoticeVO implements Serializable {

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

    @Schema(description = "优先级 1=普通 2=重要 3=紧急")
    private Integer priority;

    @Schema(description = "优先级名称")
    private String priorityName;

    @Schema(description = "阅读状态 0=未读 1=已读")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "发布者姓名")
    private String publisherName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}