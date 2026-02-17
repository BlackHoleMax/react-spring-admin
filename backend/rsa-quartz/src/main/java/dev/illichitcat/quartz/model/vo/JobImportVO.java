package dev.illichitcat.quartz.model.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import dev.illichitcat.quartz.model.vo.converter.ConcurrentConverter;
import dev.illichitcat.quartz.model.vo.converter.JobStatusConverter;
import dev.illichitcat.quartz.model.vo.converter.MisfirePolicyConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务导入 VO
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobImportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    @ColumnWidth(20)
    private String jobName;

    /**
     * 任务组名
     */
    @ExcelProperty(value = "任务组名")
    @ColumnWidth(20)
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @ExcelProperty(value = "调用目标")
    @ColumnWidth(30)
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @ExcelProperty(value = "cron表达式")
    @ColumnWidth(25)
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @ExcelProperty(value = "错失执行策略", converter = MisfirePolicyConverter.class)
    @ColumnWidth(20)
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @ExcelProperty(value = "并发执行", converter = ConcurrentConverter.class)
    @ColumnWidth(15)
    private String concurrent;

    /**
     * 状态（0正常 1暂停）
     */
    @ExcelProperty(value = "状态", converter = JobStatusConverter.class)
    @ColumnWidth(15)
    private String status;

    /**
     * 备注信息
     */
    @ExcelProperty(value = "备注")
    @ColumnWidth(30)
    private String remark;
}