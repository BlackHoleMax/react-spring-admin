package dev.illichitcat.quartz.model.vo.converter;

import dev.illichitcat.common.converter.AbstractStatusConverter;

/**
 * 并发执行转换器
 *
 * @author Illichitcat
 * @since 2025/01/16
 */
public class ConcurrentConverter extends AbstractStatusConverter {

    @Override
    protected String getStatus0Name() {
        return "允许";
    }

    @Override
    protected String getStatus1Name() {
        return "禁止";
    }

    @Override
    protected String getDefaultValue() {
        return "1";
    }
}