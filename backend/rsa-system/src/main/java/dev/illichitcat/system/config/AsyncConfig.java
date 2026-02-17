package dev.illichitcat.system.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异步线程池配置
 * <p>
 * 使用 JDK 21 虚拟线程（Virtual Threads）提升并发性能
 *
 * @author Illichitcat
 * @since 2026/01/13
 */
@Getter
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    public final Integer AwaitTerminationTimeout = 30;
    /**
     * 线程名称计数器，确保线程名唯一
     */
    private final AtomicLong threadCounter = new AtomicLong(1);

    /**
     * 自定义虚拟线程工厂
     * <p>
     * 遵循阿里规范：为线程指定有意义的名称，便于问题排查和系统监控
     * </p>
     */
    private ThreadFactory createVirtualThreadFactory(String namePrefix) {
        return Thread.ofVirtual()
                // 格式: namePrefix-1, namePrefix-2, ...
                .name(namePrefix + "-", 0)
                .uncaughtExceptionHandler((thread, throwable) -> {
                    // 统一处理未捕获异常，避免异常被静默吞噬
                    log.error("虚拟线程 [{}] 执行发生未捕获异常", thread.getName(), throwable);
                    // 此处可根据需要接入监控系统（如Sentinel、SkyWalking）
                })
                .factory();
    }

    /**
     * 创建虚拟线程执行器（主执行器）
     * <p>
     * 适用于Spring的@Async注解、操作日志、登录日志等异步处理场景。
     * 标记为@Primary，作为默认的异步执行器。
     * </p>
     *
     * @return Executor 虚拟线程执行器
     */
    @Primary
    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadFactory factory = createVirtualThreadFactory("async-vt");
        Executor executor = java.util.concurrent.Executors.newThreadPerTaskExecutor(factory);

        log.info("虚拟线程执行器(asyncExecutor)初始化成功，线程名前缀: async-vt-");
        return executor;
    }

    /**
     * 虚拟线程执行器服务
     * <p>
     * 用于CompletableFuture.runAsync、手动提交任务等需要ExecutorService的场景。
     * 与asyncExecutor使用相同的配置，确保行为一致性。
     * </p>
     *
     * @return ExecutorService 虚拟线程执行器服务
     */
    @Bean("virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        ThreadFactory factory = createVirtualThreadFactory("vt-service");
        ExecutorService executorService = java.util.concurrent.Executors.newThreadPerTaskExecutor(factory);

        log.info("虚拟线程执行器服务(virtualThreadExecutor)初始化成功，线程名前缀: vt-service-");
        return executorService;
    }

    /**
     * 注册Shutdown Hook，确保应用关闭时资源被清理
     * <p>
     * 虽然虚拟线程资源开销小，但显式关闭是良好的编程习惯
     * </p>
     */
    @Bean
    public Object shutdownHookRegistration(@Qualifier("virtualThreadExecutor") ExecutorService executorService) {
        // 注册JVM关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("应用关闭，开始清理虚拟线程执行器资源...");
            try {
                // 优雅关闭：停止接收新任务，等待已提交任务完成
                executorService.shutdown();

                // 等待一段时间让任务完成
                if (!executorService.awaitTermination(AwaitTerminationTimeout, TimeUnit.SECONDS)) {
                    log.warn("虚拟线程执行器未在30秒内完全停止，尝试强制关闭");
                    executorService.shutdownNow(); // 强制取消正在执行的任务
                }
                log.info("虚拟线程执行器资源清理完成");
            } catch (InterruptedException e) {
                log.warn("虚拟线程执行器关闭过程被中断", e);
                Thread.currentThread().interrupt(); // 保持中断状态
                executorService.shutdownNow();
            }
        }));

        return new Object();
    }

}