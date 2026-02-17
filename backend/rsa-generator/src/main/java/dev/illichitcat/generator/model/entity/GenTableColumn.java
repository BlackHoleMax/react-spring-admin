package dev.illichitcat.generator.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码生成业务表字段
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Data
@TableName("gen_table_column")
@Schema(description = "代码生成业务表字段")
public class GenTableColumn {

    @TableId(value = "column_id", type = IdType.AUTO)
    @Schema(description = "列ID")
    private Long columnId;

    @Schema(description = "归属表ID")
    private Long tableId;

    @Schema(description = "列名称")
    private String columnName;

    @Schema(description = "列描述")
    private String columnComment;

    @Schema(description = "列类型")
    private String columnType;

    @Schema(description = "Java类型")
    private String javaType;

    @Schema(description = "Java字段名")
    private String javaField;

    @Schema(description = "是否主键（1是）")
    private String isPk;

    @Schema(description = "是否自增（1是）")
    private String isIncrement;

    @Schema(description = "是否必填（1是）")
    private String isRequired;

    @Schema(description = "是否为插入字段（1是）")
    private String isInsert;

    @Schema(description = "是否编辑字段（1是）")
    private String isEdit;

    @Schema(description = "是否列表字段（1是）")
    private String isList;

    @Schema(description = "是否查询字段（1是）")
    private String isQuery;

    @Schema(description = "查询方式（等于、不等于、大于、小于、范围）")
    private String queryType;

    @Schema(description = "显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）")
    private String htmlType;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}