package dev.illichitcat.quartz.model.vo.converter;

import dev.illichitcat.common.converter.AbstractStatusConverter;

/**
 * 任务状态转换器
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
public class JobStatusConverter extends AbstractStatusConverter {

    @Override
    protected String getStatus0Name() {
        return "正常";
    }

    @Override
    protected String getStatus1Name() {
        return "暂停";
    }

    @Override
    protected String getDefaultValue() {
        return "1";
    }
}