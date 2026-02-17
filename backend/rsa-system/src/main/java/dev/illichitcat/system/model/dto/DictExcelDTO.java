package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "字典Excel导入导出DTO")
public class DictExcelDTO {

    @ExcelProperty("字典ID")
    @Schema(description = "字典ID")
    private Long id;

    @ExcelProperty("字典名称")
    @Schema(description = "字典名称")
    private String dictName;

    @ExcelProperty("字典编码")
    @Schema(description = "字典编码")
    private String dictCode;

    @ExcelProperty("排序")
    @Schema(description = "排序")
    private Integer sort;

    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

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