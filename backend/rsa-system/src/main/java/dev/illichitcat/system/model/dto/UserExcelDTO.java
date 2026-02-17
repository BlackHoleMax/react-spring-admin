package dev.illichitcat.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户Excel导入导出DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@Schema(description = "用户Excel导入导出DTO")
public class UserExcelDTO {

    @ExcelProperty("用户ID")
    @Schema(description = "用户ID")
    private Long id;

    @ExcelProperty("登录账号")
    @Schema(description = "登录账号")
    private String username;

    @ExcelProperty("密码")
    @Schema(description = "密码")
    private String password;

    @ExcelProperty("显示名")
    @Schema(description = "显示名")
    private String nickname;

    @ExcelProperty("头像URL")
    @Schema(description = "头像URL")
    private String avatar;

    @ExcelProperty("邮箱")
    @Schema(description = "邮箱")
    private String email;

    @ExcelProperty("手机号")
    @Schema(description = "手机号")
    private String phone;

    @ExcelProperty("状态")
    @Schema(description = "状态 1 正常 0 禁用")
    private Integer status;

    @ExcelProperty("角色编码")
    @Schema(description = "角色编码，多个用逗号分隔")
    private String roleCodes;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}