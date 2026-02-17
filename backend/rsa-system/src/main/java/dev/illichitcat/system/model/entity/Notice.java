package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知公告实体类
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_notice")
@Schema(description = "通知公告表")
public class Notice {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告类型 1=系统公告 2=活动通知")
    private Integer type;

    @Schema(description = "发布范围 1=全部用户 2=指定角色")
    private Integer targetType;

    @Schema(description = "指定角色ID列表(JSON数组)")
    private String targetRoles;

    @Schema(description = "优先级 1=普通 2=重要 3=紧急")
    private Integer priority;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态 1=草稿 2=已发布 3=已撤回")
    private Integer status;

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

    @TableLogic
    @Schema(description = "逻辑删除 0 正常 1 删除")
    private Integer delFlag;

    @Schema(description = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}