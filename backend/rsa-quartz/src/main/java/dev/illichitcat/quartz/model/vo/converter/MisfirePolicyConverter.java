package dev.illichitcat.quartz.model.vo.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;

/**
 * 错失执行策略转换器
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
public class MisfirePolicyConverter implements Converter<String> {

    /**
     * 错失执行策略常量
     */
    private static final String POLICY_1 = "1";
    private static final String POLICY_2 = "2";
    private static final String POLICY_3 = "3";

    private static final String POLICY_1_NAME = "立即执行";
    private static final String POLICY_2_NAME = "执行一次";
    private static final String POLICY_3_NAME = "放弃执行";

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
            return POLICY_3;
        }

        return switch (stringValue) {
            case POLICY_1_NAME -> POLICY_1;
            case POLICY_2_NAME -> POLICY_2;
            case POLICY_3_NAME -> POLICY_3;
            default -> stringValue;
        };
    }

    @Override
    public WriteCellData<String> convertToExcelData(
            String value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration
    ) {
        String displayValue = switch (value) {
            case POLICY_1 -> POLICY_1_NAME;
            case POLICY_2 -> POLICY_2_NAME;
            case POLICY_3 -> POLICY_3_NAME;
            default -> value;
        };
        return new WriteCellData<>(displayValue);
    }
}