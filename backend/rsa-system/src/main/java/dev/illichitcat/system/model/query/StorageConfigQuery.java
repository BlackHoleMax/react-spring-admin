package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 存储配置查询对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "存储配置查询对象")
public class StorageConfigQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置key（模糊查询）")
    private String configKey;

    @Schema(description = "存储服务商（minio、oss、cos等）")
    private String storageProvider;

    @Schema(description = "桶名称（模糊查询）")
    private String bucketName;

    @Schema(description = "是否默认（0否 1是）")
    private Integer isDefault;

    @Schema(description = "状态（0禁用 1启用）")
    private Integer status;
}