package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对象存储配置视图对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "对象存储配置视图对象")
public class StorageConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "访问站点")
    private String endpoint;

    @Schema(description = "自定义域名")
    private String domain;

    @Schema(description = "桶名称")
    private String bucketName;

    @Schema(description = "前缀")
    private String prefix;

    @Schema(description = "域")
    private String region;

    @Schema(description = "桶权限类型（private-私有、public-read-公共读）")
    private String bucketAcl;

    @Schema(description = "访问密钥")
    private String accessKey;

    @Schema(description = "密钥")
    private String secretKey;

    @Schema(description = "状态（0-禁用 1-启用）")
    private Integer status;

    @Schema(description = "是否HTTPS（0-否 1-是）")
    private Integer isHttps;

    @Schema(description = "存储提供商（minio-阿里云OSS、qiniu-七牛云、cos-腾讯云）")
    private String storageProvider;

    @Schema(description = "是否默认（0-否 1-是）")
    private Integer isDefault;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}