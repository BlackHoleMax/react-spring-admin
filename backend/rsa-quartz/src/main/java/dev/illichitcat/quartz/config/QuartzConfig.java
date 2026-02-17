package dev.illichitcat.quartz.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz 定时任务配置类
 *
 * @author Illichitcat
 * @since 2025/01/05
 */
@Configuration
public class QuartzConfig {

    /**
     * 配置 SchedulerFactoryBean
     * Spring Boot 会自动检测到这个 Bean 并在应用启动时创建并启动 Scheduler
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 设置调度器自动启动
        factory.setAutoStartup(true);
        // 延迟启动（秒），避免应用启动时数据源未就绪
        factory.setStartupDelay(10);
        // 设置调度器名称
        factory.setSchedulerName("QuartzScheduler");
        // 覆盖已存在的任务
        factory.setOverwriteExistingJobs(true);
        return factory;
    }

    /**
     * 注入 Scheduler Bean
     * 这个 Bean 会被 JobServiceImpl 使用
     */
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws Exception {
        return factory.getScheduler();
    }
}