package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "权限DTO")
public class PermissionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "权限标识")
    private String perm;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "关联菜单ID")
    private Long menuId;
}
