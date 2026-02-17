package dev.illichitcat.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告查询参数
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知公告查询参数")
public class NoticeQuery extends PageQuery {

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告类型 1=系统公告 2=活动通知")
    private Integer type;

    @Schema(description = "状态 1=草稿 2=已发布 3=已撤回")
    private Integer status;

    @Schema(description = "优先级 1=普通 2=重要 3=紧急")
    private Integer priority;
}