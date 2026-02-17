package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件查询对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "文件查询对象")
public class FileQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件名（模糊查询）")
    private String fileName;

    @Schema(description = "原始文件名（模糊查询）")
    private String originalName;

    @Schema(description = "文件后缀")
    private String fileSuffix;

    @Schema(description = "文件分类（image图片、video视频、document文档、other其他）")
    private String fileCategory;

    @Schema(description = "存储服务商（minio、oss、cos等）")
    private String storageProvider;

    @Schema(description = "上传人ID")
    private Long uploadUserId;

    @Schema(description = "创建时间-开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;
}