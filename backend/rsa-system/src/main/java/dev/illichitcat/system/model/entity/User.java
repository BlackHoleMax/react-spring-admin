package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@Schema(description = "用户表")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "BCrypt 密文")
    private String password;

    @Schema(description = "显示名")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别 0=男 1=女 2=其他 3=保密")
    private Integer gender;

    @Schema(description = "状态 1 正常 0 禁用")
    private Integer status;

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