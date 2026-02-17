package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2026/01/09
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "操作日志Excel导入导出DTO")
public class OperLogExcelDTO {

    @ExcelProperty("日志ID")
    @Schema(description = "日志ID")
    private Long id;

    @ExcelProperty("模块标题")
    @Schema(description = "模块标题")
    private String title;

    @ExcelProperty("业务类型")
    @Schema(description = "业务类型")
    private String businessTypeStr;

    @ExcelProperty("方法名称")
    @Schema(description = "方法名称")
    private String method;

    @ExcelProperty("请求方式")
    @Schema(description = "请求方式")
    private String requestMethod;

    @ExcelProperty("操作人员")
    @Schema(description = "操作人员")
    private String operName;

    @ExcelProperty("请求URL")
    @Schema(description = "请求URL")
    private String operUrl;

    @ExcelProperty("操作IP")
    @Schema(description = "操作IP")
    private String operIp;

    @ExcelProperty("操作状态")
    @Schema(description = "操作状态")
    private String statusStr;

    @ExcelProperty("耗时(ms)")
    @Schema(description = "耗时")
    private Long costTime;

    @ExcelProperty("操作时间")
    @Schema(description = "操作时间")
    private LocalDateTime operTime;
}