package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统菜单实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_menu")
@Schema(description = "菜单表")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "父ID，0=顶级")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "路由路径/外链")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "重定向地址")
    private String redirect;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否隐藏 0 显示 1 隐藏")
    private Integer hidden;

    @Schema(description = "是否外链 0 内部 1 外链")
    private Integer external;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "状态 1 启用 0 禁用")
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