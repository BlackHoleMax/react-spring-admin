package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@TableName("sys_oper_log")
public class OperLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private Integer businessType;

    private String method;

    private String requestMethod;

    private Integer operatorType;

    private String operName;

    private String operUrl;

    private String operIp;

    private String operParam;

    private String jsonResult;

    private Integer status;

    private String errorMsg;

    private LocalDateTime operTime;

    private Long costTime;
}
