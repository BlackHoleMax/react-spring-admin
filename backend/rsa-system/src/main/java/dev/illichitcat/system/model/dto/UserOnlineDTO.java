package dev.illichitcat.system.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 在线用户DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "在线用户DTO")
public class UserOnlineDTO {

    @Schema(description = "会话编号")
    private String id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "显示名称")
    private String nickname;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "在线状态 on_line在线 off_line离线")
    private String status;

    @Schema(description = "会话创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "最后访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastTime;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "在线时长（分钟）")
    private Long onlineMinutes;
}