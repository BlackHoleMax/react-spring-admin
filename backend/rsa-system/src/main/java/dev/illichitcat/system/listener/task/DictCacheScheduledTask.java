package dev.illichitcat.system.listener.task;

import dev.illichitcat.system.config.DictCacheProperties;
import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.service.DictCacheService;
import dev.illichitcat.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 字典缓存定时任务
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Component
public class DictCacheScheduledTask {

    @Autowired
    private DictCacheService dictCacheService;

    @Autowired
    private DictService dictService;

    @Autowired
    private DictCacheProperties cacheProperties;

    /**
     * 定时检查字典更新情况，刷新缓存
     */
    @Scheduled(cron = "#{${dict.cache.scheduled.enabled} ? '0 */10 * * * ?' : '-'}")
    public void refreshDictCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始定时刷新字典缓存");

            // 获取所有字典
            List<Dict> dictList = dictService.list();
            if (dictList.isEmpty()) {
                log.info("没有字典数据需要刷新");
                return;
            }

            int refreshedCount = 0;
            for (Dict dict : dictList) {
                try {
                    // 检查字典是否需要刷新
                    if (shouldRefreshDict(dict)) {
                        // 清除缓存，下次访问时会重新加载
                        dictCacheService.evictDictCache(dict.getId());
                        dictCacheService.evictDictCache(dict.getDictCode());
                        refreshedCount++;

                        log.debug("刷新字典缓存, dictId={}, dictCode={}", dict.getId(), dict.getDictCode());
                    }
                } catch (Exception e) {
                    log.error("刷新字典缓存失败, dictId={}, dictCode={}", dict.getId(), dict.getDictCode(), e);
                }
            }

            log.info("定时刷新字典缓存完成, 总字典数={}, 刷新数={}", dictList.size(), refreshedCount);
        } catch (Exception e) {
            log.error("定时刷新字典缓存失败", e);
        }
    }

    /**
     * 每天凌晨2点执行全量缓存预热
     */
    @Scheduled(cron = "#{${dict.cache.scheduled.enabled} ? '${dict.cache.scheduled.warm-up-cron}' : '-'}")
    public void warmUpCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始执行字典缓存预热任务");
            dictCacheService.warmUpCache();
            log.info("字典缓存预热任务完成");
        } catch (Exception e) {
            log.error("字典缓存预热任务失败", e);
        }
    }

    /**
     * 判断字典是否需要刷新
     *
     * @param dict 字典对象
     * @return 是否需要刷新
     */
    private boolean shouldRefreshDict(Dict dict) {
        if (dict == null) {
            return false;
        }

        // 检查字典是否在时间窗口内有更新
        if (dict.getUpdateTime() != null) {
            LocalDateTime updateTime = dict.getUpdateTime();
            LocalDateTime now = LocalDateTime.now();
            long minutesBetween = ChronoUnit.MINUTES.between(updateTime, now);
            return minutesBetween <= 10;
        }

        return false;
    }
}