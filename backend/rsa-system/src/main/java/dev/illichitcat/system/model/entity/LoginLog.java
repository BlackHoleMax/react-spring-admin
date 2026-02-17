package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统登录日志实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_login_log")
@Schema(description = "登录日志表")
public class LoginLog {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "浏览器UA")
    private String userAgent;

    @Schema(description = "登录状态 1 成功 0 失败")
    private Integer status;

    @Schema(description = "登录信息")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}