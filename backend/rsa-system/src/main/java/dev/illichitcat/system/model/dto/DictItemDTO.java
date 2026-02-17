package dev.illichitcat.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典项DTO
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "字典项DTO")
public class DictItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "字典ID")
    private Long dictId;

    @Schema(description = "字典项文本")
    private String itemText;

    @Schema(description = "字典项值")
    private String itemValue;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;
}
