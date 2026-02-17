package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色权限关联DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "角色权限关联DTO")
public class RolePermDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID列表")
    private List<Long> permIds;
}
