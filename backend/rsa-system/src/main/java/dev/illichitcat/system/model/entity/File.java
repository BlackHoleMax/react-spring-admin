package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件管理实体类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_file")
@Schema(description = "文件管理表")
public class File implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    @Schema(description = "文件类型（MIME类型）")
    private String fileType;

    @Schema(description = "文件分类（image图片、video视频、document文档、other其他）")
    private String fileCategory;

    @Schema(description = "存储服务商（minio、oss、cos等）")
    private String storageProvider;

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "上传人ID")
    private Long uploadUserId;

    @Schema(description = "上传人姓名")
    private String uploadUserName;

    @TableLogic
    @Schema(description = "逻辑删除 0 正常 1 删除")
    private Integer delFlag;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}