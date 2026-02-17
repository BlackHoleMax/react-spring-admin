package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单查询参数
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Schema(description = "菜单查询参数")
public class MenuQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}
