package dev.illichitcat.quartz.utils;

import dev.illichitcat.common.utils.SpringUtils;
import dev.illichitcat.quartz.model.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 任务执行工具
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Slf4j
public class JobInvokeUtil {

    /**
     * 执行方法
     *
     * @param job 系统任务
     */
    public static void invokeMethod(Job job) throws Exception {
        String invokeTarget = job.getInvokeTarget();
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        Object[] methodParams = getMethodParams(invokeTarget);

        if (!isValidClassName(beanName)) {
            Object bean = SpringUtils.getBean(beanName);
            invokeMethod(bean, methodName, methodParams);
        } else {
            Object bean = Class.forName(beanName).getDeclaredConstructor().newInstance();
            invokeMethod(bean, methodName, methodParams);
        }
    }

    /**
     * 调用任务方法
     *
     * @param bean         目标对象
     * @param methodName   方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, Object[] methodParams)
            throws Exception {
        if (methodParams != null && methodParams.length > 0) {
            Class<?>[] parameterTypes = new Class[methodParams.length];
            for (int i = 0; i < methodParams.length; i++) {
                parameterTypes[i] = methodParams[i].getClass();
            }
            Method method = bean.getClass().getMethod(methodName, parameterTypes);
            method.invoke(bean, methodParams);
        } else {
            Method method = bean.getClass().getMethod(methodName);
            method.invoke(bean);
        }
    }

    /**
     * 获取bean名称
     *
     * @param invokeTarget 调用目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget) {
        String beanName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringBeforeLast(beanName, ".");
    }

    /**
     * 获取方法名称
     *
     * @param invokeTarget 调用目标字符串
     * @return 方法名称
     */
    public static String getMethodName(String invokeTarget) {
        String beanName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringAfterLast(beanName, ".");
    }

    /**
     * 获取方法参数
     *
     * @param invokeTarget 调用目标字符串
     * @return 方法参数
     */
    public static Object[] getMethodParams(String invokeTarget) {
        String methodParams = StringUtils.substringBetween(invokeTarget, "(", ")");
        if (methodParams == null || methodParams.isEmpty()) {
            return new Object[0];
        }
        String[] params = methodParams.split(",");
        Object[] objects = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String param = params[i].trim();
            if (param.isEmpty()) {
                continue;
            }
            // 去除引号
            if (param.startsWith("'") && param.endsWith("'")) {
                objects[i] = param.substring(1, param.length() - 1);
            } else if (param.startsWith("\"") && param.endsWith("\"")) {
                objects[i] = param.substring(1, param.length() - 1);
            } else if ("true".equals(param)) {
                objects[i] = true;
            } else if ("false".equals(param)) {
                objects[i] = false;
            } else if (param.endsWith("L")) {
                objects[i] = Long.parseLong(param.substring(0, param.length() - 1));
            } else if (param.endsWith("D")) {
                objects[i] = Double.parseDouble(param.substring(0, param.length() - 1));
            } else if (param.contains(".")) {
                objects[i] = Double.parseDouble(param);
            } else {
                objects[i] = Integer.parseInt(param);
            }
        }
        return objects;
    }

    /**
     * 校验是否为为class包名
     *
     * @param invokeTarget 字符串
     * @return true是 false否
     */
    public static boolean isValidClassName(String invokeTarget) {
        return invokeTarget != null && invokeTarget.contains(".");
    }
}
