package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限查询参数
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "权限查询参数")
public class PermissionQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限标识")
    private String perm;

    @Schema(description = "菜单ID")
    private Long menuId;
}
