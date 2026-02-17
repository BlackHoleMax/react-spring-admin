package dev.illichitcat.system.model.vo;

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
 * 登录日志导出 VO
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(20)
@ContentRowHeight(18)
public class LoginLogExportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @ExcelProperty("日志ID")
    @ColumnWidth(15)
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty("用户ID")
    @ColumnWidth(15)
    private Long userId;

    /**
     * 登录账号
     */
    @ExcelProperty("登录账号")
    @ColumnWidth(20)
    private String username;

    /**
     * 登录IP
     */
    @ExcelProperty("登录IP")
    @ColumnWidth(20)
    private String ip;

    /**
     * 浏览器UA
     */
    @ExcelProperty("浏览器信息")
    @ColumnWidth(30)
    private String userAgent;

    /**
     * 登录状态 1 成功 0 失败
     */
    @ExcelProperty("登录状态")
    @ColumnWidth(15)
    private String status;

    /**
     * 登录信息
     */
    @ExcelProperty("登录信息")
    @ColumnWidth(30)
    private String msg;

    /**
     * 登录时间
     */
    @ExcelProperty("登录时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
}