package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对象存储配置实体类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_storage_config")
@Schema(description = "对象存储配置实体类")
public class StorageConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "访问站点")
    private String endpoint;

    @Schema(description = "自定义域名")
    private String customDomain;

    @Schema(description = "桶名称")
    private String bucketName;

    @Schema(description = "前缀")
    private String prefix;

    @Schema(description = "域")
    private String region;

    @Schema(description = "存储服务商（minio、oss、cos等）")
    private String storageProvider;

    @Schema(description = "访问密钥")
    private String accessKey;

    @Schema(description = "密钥")
    private String secretKey;

    @Schema(description = "桶权限类型（private私有、public-read公开读、public-read-write公开读写）")
    private String bucketPermission;

    @Schema(description = "是否HTTPS（0否 1是）")
    private Integer isHttps;

    @Schema(description = "是否默认（0否 1是）")
    private Integer isDefault;

    @Schema(description = "备注")
    private String remark;

    @TableLogic
    @Schema(description = "逻辑删除 0 正常 1 删除")
    private Integer delFlag;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}