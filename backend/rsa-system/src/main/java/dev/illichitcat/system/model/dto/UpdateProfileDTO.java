package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "更新个人资料DTO")
public class UpdateProfileDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别 0=男 1=女 2=其他 3=保密")
    private Integer gender;
}
