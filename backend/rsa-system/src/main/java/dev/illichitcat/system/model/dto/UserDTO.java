package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户DTO类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "用户DTO")
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "登录账号不能为空")
    @Size(min = 3, max = 20, message = "登录账号长度必须在3-20之间")
    @Schema(description = "登录账号")
    private String username;

    @NotBlank(message = "显示名不能为空")
    @Size(max = 50, message = "显示名长度不能超过50")
    @Schema(description = "显示名")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    @Schema(description = "状态 1 正常 0 禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}