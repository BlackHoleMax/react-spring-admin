package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "修改密码DTO")
public class UpdatePasswordDTO {

    @Schema(description = "旧密码")
    private String oldPassword;

    @Schema(description = "新密码")
    private String newPassword;
}
