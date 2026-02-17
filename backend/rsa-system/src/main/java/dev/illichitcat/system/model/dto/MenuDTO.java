package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "菜单DTO")
public class MenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "父ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "重定向地址")
    private String redirect;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否隐藏")
    private Integer hidden;

    @Schema(description = "是否外链")
    private Integer external;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "状态")
    private Integer status;
}
