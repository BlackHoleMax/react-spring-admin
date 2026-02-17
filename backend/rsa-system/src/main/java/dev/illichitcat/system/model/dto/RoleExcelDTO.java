package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "角色Excel导入导出DTO")
public class RoleExcelDTO {

    @ExcelProperty("角色ID")
    @Schema(description = "角色ID")
    private Long id;

    @ExcelProperty("角色名称")
    @Schema(description = "角色名称")
    private String name;

    @ExcelProperty("角色编码")
    @Schema(description = "角色编码")
    private String code;

    @ExcelProperty("排序")
    @Schema(description = "排序")
    private Integer sort;

    @ExcelProperty("状态")
    @Schema(description = "状态 1 启用 0 禁用")
    private Integer status;

    @ExcelProperty("权限标识")
    @Schema(description = "权限标识，多个用逗号分隔")
    private String permissions;

    @ExcelProperty("菜单ID")
    @Schema(description = "菜单ID，多个用逗号分隔")
    private String menuIds;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}