package dev.illichitcat.system.config;

import lombok.Getter;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 业务类型（0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=清空）
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别（0=其它,1=后台用户,2=手机端用户）
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;

    /**
     * 业务类型枚举
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Getter
    enum BusinessType {
        /**
         * 业务类型
         */
        OTHER(0, "其它"),
        INSERT(1, "新增"),
        UPDATE(2, "修改"),
        DELETE(3, "删除"),
        GRANT(4, "授权"),
        EXPORT(5, "导出"),
        IMPORT(6, "导入"),
        CLEAN(7, "清空");

        private final int code;
        private final String desc;

        BusinessType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

    }

    /**
     * 操作人类别枚举
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Getter
    enum OperatorType {
        /**
         * 操作人类型
         */
        OTHER(0, "其它"),
        MANAGE(1, "后台用户"),
        MOBILE(2, "手机端用户");

        private final int code;
        private final String desc;

        OperatorType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

    }
}
