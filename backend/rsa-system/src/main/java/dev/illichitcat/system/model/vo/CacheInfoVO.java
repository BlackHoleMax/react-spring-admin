package dev.illichitcat.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 缓存监控信息视图对象
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Data
@Schema(description = "缓存监控信息视图对象")
public class CacheInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Redis服务器信息")
    private Map<String, String> info;

    @Schema(description = "数据库键数量")
    private Integer dbSize;

    @Schema(description = "命令统计列表")
    private List<CommandStatVO> commandStats;
}