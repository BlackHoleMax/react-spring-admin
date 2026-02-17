package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "菜单Excel导入导出DTO")
public class MenuExcelDTO {

    @ExcelProperty("菜单ID")
    @Schema(description = "菜单ID")
    private Long id;

    @ExcelProperty("父菜单ID")
    @Schema(description = "父菜单ID，0=顶级")
    private Long parentId;

    @ExcelProperty("菜单名称")
    @Schema(description = "菜单名称")
    private String name;

    @ExcelProperty("路由路径")
    @Schema(description = "路由路径/外链")
    private String path;

    @ExcelProperty("组件路径")
    @Schema(description = "组件路径")
    private String component;

    @ExcelProperty("重定向地址")
    @Schema(description = "重定向地址")
    private String redirect;

    @ExcelProperty("图标")
    @Schema(description = "图标")
    private String icon;

    @ExcelProperty("排序")
    @Schema(description = "排序")
    private Integer sort;

    @ExcelProperty("是否隐藏")
    @Schema(description = "是否隐藏 0 显示 1 隐藏")
    private Integer hidden;

    @ExcelProperty("是否外链")
    @Schema(description = "是否外链 0 内部 1 外链")
    private Integer external;

    @ExcelProperty("权限标识")
    @Schema(description = "权限标识")
    private String perms;

    @ExcelProperty("状态")
    @Schema(description = "状态 1 启用 0 禁用")
    private Integer status;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}