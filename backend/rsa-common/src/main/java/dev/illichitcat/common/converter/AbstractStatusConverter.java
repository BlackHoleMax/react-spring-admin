package dev.illichitcat.common.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;

/**
 * 抽象状态转换器
 * <p>
 * 用于处理 Excel 导入导出中常见的 0/1 状态值转换
 *
 * @author Illichitcat
 * @since 2025/01/27
 */
public abstract class AbstractStatusConverter implements Converter<String> {

    /**
     * 状态值：停用/禁用
     */
    private static final String STATUS_DISABLED = "0";

    /**
     * 状态值：启用/正常
     */
    private static final String STATUS_ENABLED = "1";

    /**
     * 获取状态值 "0" 对应的显示名称
     *
     * @return 状态值 "0" 的显示名称
     */
    protected abstract String getStatus0Name();

    /**
     * 获取状态值 "1" 对应的显示名称
     *
     * @return 状态值 "1" 的显示名称
     */
    protected abstract String getStatus1Name();

    /**
     * 获取默认值（当 Excel 中为空时）
     *
     * @return 默认状态值
     */
    protected String getDefaultValue() {
        return STATUS_ENABLED;
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(
            ReadCellData<?> cellData,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration
    ) {
        String stringValue = cellData.getStringValue();
        if (stringValue == null) {
            return getDefaultValue();
        }

        String status0Name = getStatus0Name();
        String status1Name = getStatus1Name();

        if (status0Name.equals(stringValue)) {
            return STATUS_DISABLED;
        } else if (status1Name.equals(stringValue)) {
            return STATUS_ENABLED;
        } else {
            return stringValue;
        }
    }

    @Override
    public WriteCellData<String> convertToExcelData(
            String value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration
    ) {
        String displayValue;
        if (STATUS_DISABLED.equals(value)) {
            displayValue = getStatus0Name();
        } else if (STATUS_ENABLED.equals(value)) {
            displayValue = getStatus1Name();
        } else {
            displayValue = value;
        }
        return new WriteCellData<>(displayValue);
    }
}