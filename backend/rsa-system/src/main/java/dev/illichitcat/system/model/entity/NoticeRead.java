package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知已读记录实体类
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_notice_read")
@Schema(description = "通知已读记录表")
public class NoticeRead {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "通知ID")
    private Long noticeId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}