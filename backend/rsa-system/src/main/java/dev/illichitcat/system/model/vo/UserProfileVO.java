package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "用户个人信息VO")
public class UserProfileVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别 0=男 1=女 2=其他 3=保密")
    private Integer gender;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "登录次数")
    private Long loginCount;
}
