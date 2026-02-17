package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Redis键详细信息视图对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "Redis键详细信息视图对象")
public class KeyDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "键值")
    private String value;

    @Schema(description = "键类型")
    private String type;

    @Schema(description = "过期时间(秒)，-1表示永不过期，-2表示键不存在")
    private Long ttl;
}