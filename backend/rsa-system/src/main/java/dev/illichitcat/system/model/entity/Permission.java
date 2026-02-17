package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
@Schema(description = "权限表")
public class Permission {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "权限标识")
    private String perm;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "关联菜单ID")
    private Long menuId;

    @TableField(exist = false)
    @Schema(description = "关联菜单名称（非数据库字段）")
    private String menuName;
}