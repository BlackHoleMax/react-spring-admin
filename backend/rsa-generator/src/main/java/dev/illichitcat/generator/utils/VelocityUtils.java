package dev.illichitcat.generator.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Properties;

/**
 * Velocity模板引擎工具类
 *
 * @author Illichitcat
 * @since 2026/01/15
 */
public class VelocityUtils {

    private static final VelocityEngine VELOCITY_ENGINE;

    static {
        try {
            Properties props = new Properties();
            props.put("resource.loaders", "class");
            props.put("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            props.put("resource.default_encoding", "UTF-8");
            props.put("output.encoding", "UTF-8");
            VELOCITY_ENGINE = new VelocityEngine(props);
        } catch (Exception e) {
            throw new RuntimeException("初始化Velocity引擎失败", e);
        }
    }

    /**
     * 渲染模板
     *
     * @param templateName 模板名称
     * @param context      上下文
     * @return 渲染后的字符串
     */
    public static String renderTemplate(String templateName, VelocityContext context) {
        try {
            Template template = VELOCITY_ENGINE.getTemplate(templateName, "UTF-8");
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("渲染模板失败: " + templateName, e);
        }
    }
}