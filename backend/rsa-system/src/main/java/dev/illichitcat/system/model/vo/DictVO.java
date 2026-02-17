package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典视图对象
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "字典视图对象")
public class DictVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "字典项列表")
    private List<DictItemVO> items;
}
