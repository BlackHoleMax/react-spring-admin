package dev.illichitcat.generator.utils;

import dev.illichitcat.generator.model.entity.GenTable;
import dev.illichitcat.generator.model.entity.GenTableColumn;

import java.util.Arrays;

/**
 * 代码生成工具类
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
public class GenUtils {

    /**
     * 数据库列名：创建人
     */
    private static final String COLUMN_CREATE_BY = "create_by";

    /**
     * 数据库列名：创建时间
     */
    private static final String COLUMN_CREATE_TIME = "create_time";

    /**
     * 数据库列名：更新人
     */
    private static final String COLUMN_UPDATE_BY = "update_by";

    /**
     * 数据库列名：更新时间
     */
    private static final String COLUMN_UPDATE_TIME = "update_time";

    /**
     * 数据库列名：删除标志
     */
    private static final String COLUMN_DEL_FLAG = "del_flag";

    /**
     * 括号（用于解析数据库类型）
     */
    private static final String PARENTHESIS_LEFT = "(";

    /**
     * 列名后缀：名称
     */
    private static final String COLUMN_SUFFIX_NAME = "name";

    /**
     * 列名后缀：昵称
     */
    private static final String COLUMN_SUFFIX_NICK = "nick";

    /**
     * 列名后缀：状态
     */
    private static final String COLUMN_SUFFIX_STATUS = "status";

    /**
     * 列名后缀：类型
     */
    private static final String COLUMN_SUFFIX_TYPE = "type";

    /**
     * 列名后缀：时间
     */
    private static final String COLUMN_SUFFIX_TIME = "time";

    /**
     * 列名后缀：日期
     */
    private static final String COLUMN_SUFFIX_DATE = "date";

    /**
     * 数据库类型：文本
     */
    private static final String DB_TYPE_TEXT = "text";

    /**
     * 数据库类型：长文本
     */
    private static final String DB_TYPE_LONGTEXT = "longtext";

    /**
     * 数据库类型：整数
     */
    private static final String DB_TYPE_INT = "int";

    /**
     * 数据库类型：小整数
     */
    private static final String DB_TYPE_SMALLINT = "smallint";

    /**
     * 数据库类型：微小整数
     */
    private static final String DB_TYPE_TINYINT = "tinyint";

    /**
     * HTML类型：输入框
     */
    private static final String HTML_TYPE_INPUT = "input";

    /**
     * HTML类型：下拉框
     */
    private static final String HTML_TYPE_SELECT = "select";

    /**
     * HTML类型：日期时间
     */
    private static final String HTML_TYPE_DATETIME = "datetime";

    /**
     * HTML类型：文本域
     */
    private static final String HTML_TYPE_TEXTAREA = "textarea";

    /**
     * 初始化表信息
     *
     * @param genTable 表信息
     * @param operName 操作者
     */
    public static void initTable(GenTable genTable, String operName) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName("dev.illichitcat." + getModuleName(genTable.getTableName()));
        genTable.setFunctionAuthor(operName);
        genTable.setGenType("0");
        genTable.setTplCategory("crud");
    }

    /**
     * 初始化列信息
     *
     * @param column 列信息
     * @param table  表信息
     */
    public static void initColumnField(GenTableColumn column, GenTable table) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setJavaField(StringUtils.toCamelCase(columnName));
        column.setJavaType(getJavaType(dataType));
        column.setQueryType("EQ");
        column.setHtmlType(getHtmlType(dataType, columnName));

        // 设置默认插入、编辑、列表、查询状态
        if (!Arrays.asList(table.getTableId().toString(), COLUMN_CREATE_BY, COLUMN_CREATE_TIME, COLUMN_UPDATE_BY, COLUMN_UPDATE_TIME, COLUMN_DEL_FLAG).contains(columnName)) {
            column.setIsInsert("1");
            column.setIsEdit("1");
            column.setIsList("1");
            column.setIsQuery("1");
        }
    }

    /**
     * 转换为类名
     *
     * @param tableName 表名
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        String tablePrefix = getTablePrefix(tableName);
        String className = tableName.substring(tablePrefix.length());
        return StringUtils.toCamelCase(className);
    }

    /**
     * 获取模块名
     *
     * @param tableName 表名
     * @return 模块名
     */
    public static String getModuleName(String tableName) {
        String tablePrefix = getTablePrefix(tableName);
        String moduleName = tableName.substring(tablePrefix.length());
        int endIndex = moduleName.indexOf("_");
        if (endIndex > 0) {
            moduleName = moduleName.substring(0, endIndex);
        }
        return moduleName.toLowerCase();
    }

    /**
     * 获取表前缀
     *
     * @param tableName 表名
     * @return 表前缀
     */
    public static String getTablePrefix(String tableName) {
        int index = tableName.indexOf("_");
        if (index > 0) {
            return tableName.substring(0, index + 1);
        }
        return "";
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (columnType.indexOf(PARENTHESIS_LEFT) > 0) {
            return columnType.substring(0, columnType.indexOf(PARENTHESIS_LEFT));
        } else {
            return columnType;
        }
    }

    /**
     * 获取Java类型
     *
     * @param dataType 数据库类型
     * @return Java类型
     */
    public static String getJavaType(String dataType) {
        return switch (dataType) {
            case "tinyint", "smallint", "int", "integer", "mediumint" -> "Integer";
            case "bigint" -> "Long";
            case "float", "double", "decimal" -> "Double";
            case "datetime", "timestamp", "date", "time" -> "LocalDateTime";
            case "bit", "boolean" -> "Boolean";
            default -> "String";
        };
    }

    /**
     * 获取HTML类型
     *
     * @param dataType   数据库类型
     * @param columnName 列名
     * @return HTML类型
     */
    public static String getHtmlType(String dataType, String columnName) {
        if (columnName.endsWith(COLUMN_SUFFIX_NAME) || columnName.endsWith(COLUMN_SUFFIX_NICK)) {
            return HTML_TYPE_INPUT;
        } else if (columnName.endsWith(COLUMN_SUFFIX_STATUS) || columnName.endsWith(COLUMN_SUFFIX_TYPE)) {
            return HTML_TYPE_SELECT;
        } else if (columnName.endsWith(COLUMN_SUFFIX_TIME) || columnName.endsWith(COLUMN_SUFFIX_DATE)) {
            return HTML_TYPE_DATETIME;
        } else if (DB_TYPE_TEXT.equals(dataType) || DB_TYPE_LONGTEXT.equals(dataType)) {
            return HTML_TYPE_TEXTAREA;
        } else if (DB_TYPE_TINYINT.equals(dataType) || DB_TYPE_SMALLINT.equals(dataType) || DB_TYPE_INT.equals(dataType)) {
            return HTML_TYPE_INPUT;
        } else {
            return HTML_TYPE_INPUT;
        }
    }

    /**
     * 替换后缀
     *
     * @param text   文本
     * @param suffix 后缀
     * @return 替换后的文本
     */
    public static String replaceText(String text, String suffix) {
        if (text == null) {
            return "";
        }
        if (text.endsWith(suffix)) {
            return text.substring(0, text.length() - suffix.length());
        }
        return text;
    }
}