package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件管理视图对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "文件管理视图对象")
public class FileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "文件名（存储在对象存储中的名称）")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件后缀")
    private String fileSuffix;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件大小（格式化）")
    private String fileSizeDisplay;

    @Schema(description = "文件类型（MIME类型）")
    private String fileType;

    @Schema(description = "文件分类（image图片、video视频、audio音频、document文档、other其他）")
    private String fileCategory;

    @Schema(description = "存储服务商（minio、oss、cos等）")
    private String storageProvider;

    @Schema(description = "上传人ID")
    private Long uploadUserId;

    @Schema(description = "上传人姓名")
    private String uploadUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}