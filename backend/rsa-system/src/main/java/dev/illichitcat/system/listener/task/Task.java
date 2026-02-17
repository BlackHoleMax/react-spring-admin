package dev.illichitcat.system.listener.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务执行类
 *
 * @author Illichitcat
 * @since 2025/01/08
 */
@Slf4j
@Component("task")
public class Task {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 无参任务
     * 对应数据库任务：task.noParams
     */
    public void noParams() {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("执行无参任务 - 当前时间: {}", currentTime);
    }

    /**
     * 单参数任务
     * 对应数据库任务：task.params('test')
     *
     * @param param 参数
     */
    public void params(String param) {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("执行单参数任务 - 参数: {}, 当前时间: {}", param, currentTime);
    }

    /**
     * 多参数任务
     * 对应数据库任务：task.params('test1', 'test2')
     *
     * @param param1 第一个参数
     * @param param2 第二个参数
     */
    public void params(String param1, String param2) {
        String currentTime = LocalDateTime.now().format(FORMATTER);
        log.info("执行多参数任务 - 参数1: {}, 参数2: {}, 当前时间: {}", param1, param2, currentTime);
    }
}