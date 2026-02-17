package dev.illichitcat.quartz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务调度日志表 sys_job_log
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_job_log")
@Schema(description = "定时任务调度日志表")
public class JobLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "任务日志ID")
    private Long jobLogId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组名")
    private String jobGroup;

    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    @Schema(description = "日志信息")
    private String jobMessage;

    @Schema(description = "执行状态（0正常 1失败）")
    private String status;

    @Schema(description = "异常信息")
    private String exceptionInfo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "结束时间")
    private LocalDateTime stopTime;
}