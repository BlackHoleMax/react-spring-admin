package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "权限Excel导入导出DTO")
public class PermissionExcelDTO {

    @ExcelProperty("权限ID")
    @Schema(description = "权限ID")
    private Long id;

    @ExcelProperty("权限标识")
    @Schema(description = "权限标识")
    private String perm;

    @ExcelProperty("权限名称")
    @Schema(description = "权限名称")
    private String name;

    @ExcelProperty("关联菜单ID")
    @Schema(description = "关联菜单ID")
    private Long menuId;

    @ExcelProperty("菜单名称")
    @Schema(description = "菜单名称（仅用于导入时查找）")
    private String menuName;
}