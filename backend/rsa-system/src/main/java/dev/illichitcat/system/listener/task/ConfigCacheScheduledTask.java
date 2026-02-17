package dev.illichitcat.system.listener.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.illichitcat.system.config.ConfigCacheProperties;
import dev.illichitcat.system.dao.mapper.ConfigMapper;
import dev.illichitcat.system.model.entity.Config;
import dev.illichitcat.system.service.ConfigCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统配置缓存定时任务
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Component
public class ConfigCacheScheduledTask {

    @Autowired
    private ConfigCacheService configCacheService;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ConfigCacheProperties cacheProperties;

    /**
     * 定时检查配置更新情况，刷新缓存
     */
    @Scheduled(cron = "#{${config.cache.scheduled.enabled} ? '0 */15 * * * ?' : '-'}")
    public void refreshConfigCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始定时刷新系统配置缓存");

            // 获取所有启用的配置
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getStatus, 1)
                    .eq(Config::getDelFlag, 0);
            List<Config> configList = configMapper.selectList(wrapper);
            if (configList.isEmpty()) {
                log.info("没有配置数据需要刷新");
                return;
            }

            log.info("定时刷新系统配置缓存完成, 总配置数={}", configList.size());
        } catch (Exception e) {
            log.error("定时刷新系统配置缓存失败", e);
        }
    }

    /**
     * 每天凌晨4点执行全量缓存预热
     */
    @Scheduled(cron = "#{${config.cache.scheduled.enabled} ? '${config.cache.scheduled.warm-up-cron}' : '-'}")
    public void warmUpCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始执行系统配置缓存预热任务");
            configCacheService.warmUpCache();
            log.info("系统配置缓存预热任务完成");
        } catch (Exception e) {
            log.error("系统配置缓存预热任务失败", e);
        }
    }
}