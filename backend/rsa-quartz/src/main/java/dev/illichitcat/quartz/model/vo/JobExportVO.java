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
 * 定时任务导出 VO
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(20)
@ContentRowHeight(18)
public class JobExportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ExcelProperty("任务ID")
    @ColumnWidth(15)
    private Long jobId;

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
     * cron执行表达式
     */
    @ExcelProperty("cron表达式")
    @ColumnWidth(25)
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @ExcelProperty("错失执行策略")
    @ColumnWidth(20)
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @ExcelProperty("并发执行")
    @ColumnWidth(15)
    private String concurrent;

    /**
     * 状态（0正常 1暂停）
     */
    @ExcelProperty("状态")
    @ColumnWidth(15)
    private String status;

    /**
     * 创建者
     */
    @ExcelProperty("创建者")
    @ColumnWidth(15)
    private String createBy;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @ExcelProperty("更新者")
    @ColumnWidth(15)
    private String updateBy;

    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;
}