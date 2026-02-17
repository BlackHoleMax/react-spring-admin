package dev.illichitcat.quartz.model.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志导出 VO
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(20)
@ContentRowHeight(18)
public class JobLogExportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务日志ID
     */
    @ExcelProperty("日志ID")
    @ColumnWidth(15)
    private Long jobLogId;

    /**
     * 任务名称
     */
    @ExcelProperty("任务名称")
    @ColumnWidth(20)
    private String jobName;

    /**
     * 任务组名
     */
    @ExcelProperty("任务组名")
    @ColumnWidth(20)
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @ExcelProperty("调用目标")
    @ColumnWidth(30)
    private String invokeTarget;

    /**
     * 日志信息
     */
    @ExcelProperty("日志信息")
    @ColumnWidth(40)
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @ExcelProperty("执行状态")
    @ColumnWidth(15)
    private String status;

    /**
     * 异常信息
     */
    @ExcelProperty("异常信息")
    @ColumnWidth(50)
    private String exceptionInfo;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    @ExcelProperty("结束时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;
}