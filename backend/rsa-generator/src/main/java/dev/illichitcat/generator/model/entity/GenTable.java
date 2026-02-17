package dev.illichitcat.generator.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码生成业务表
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
@Data
@TableName("gen_table")
@Schema(description = "代码生成业务表")
public class GenTable {

    @TableId(value = "table_id", type = IdType.AUTO)
    @Schema(description = "表ID")
    private Long tableId;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = "实体类名称")
    private String className;

    @Schema(description = "使用的模板（crud单表 tree树表）")
    private String tplCategory;

    @Schema(description = "生成包路径")
    private String packageName;

    @Schema(description = "生成功能作者")
    private String functionAuthor;

    @Schema(description = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    @Schema(description = "生成路径（不填默认项目路径）")
    private String genPath;

    @Schema(description = "表单布局（1单列 2双列 3三列）")
    private String formLayout;

    @Schema(description = "其它生成选项")
    private String options;

    @Schema(description = "父级菜单ID（0=根菜单）")
    private Long parentMenuId;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;

    /**
     * 表列信息
     */
    @Schema(description = "表列信息")
    @TableField(exist = false)
    private List<GenTableColumn> columns;
}