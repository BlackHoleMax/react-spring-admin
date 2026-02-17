package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典查询参数
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典查询参数")
public class DictQuery extends PageQuery {

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "状态")
    private Integer status;
}
