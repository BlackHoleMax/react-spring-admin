package dev.illichitcat.system.listener.task;

import dev.illichitcat.system.config.MenuCacheProperties;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.service.MenuCacheService;
import dev.illichitcat.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 菜单缓存定时任务
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Component
public class MenuCacheScheduledTask {

    @Autowired
    private MenuCacheService menuCacheService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuCacheProperties cacheProperties;

    /**
     * 定时检查菜单更新情况，刷新缓存
     */
    @Scheduled(cron = "#{${menu.cache.scheduled.enabled} ? '0 */15 * * * ?' : '-'}")
    public void refreshMenuCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始定时刷新菜单缓存");

            // 获取所有菜单
            List<Menu> menuList = menuService.list();
            if (menuList.isEmpty()) {
                log.info("没有菜单数据需要刷新");
                return;
            }

            int refreshedCount = 0;
            for (Menu menu : menuList) {
                try {
                    // 检查菜单是否需要刷新
                    if (shouldRefreshMenu(menu)) {
                        // 清除所有菜单缓存，下次访问时会重新加载
                        menuCacheService.evictAllMenuCache();
                        refreshedCount++;
                        log.debug("刷新菜单缓存, menuId={}, menuName={}", menu.getId(), menu.getName());
                        // 找到一个需要刷新的菜单就清除所有缓存，然后退出循环
                        break;
                    }
                } catch (Exception e) {
                    log.error("刷新菜单缓存失败, menuId={}, menuName={}", menu.getId(), menu.getName(), e);
                }
            }

            log.info("定时刷新菜单缓存完成, 总菜单数={}, 刷新数={}", menuList.size(), refreshedCount);
        } catch (Exception e) {
            log.error("定时刷新菜单缓存失败", e);
        }
    }

    /**
     * 每天凌晨4点执行全量缓存预热
     */
    @Scheduled(cron = "#{${menu.cache.scheduled.enabled} ? '${menu.cache.scheduled.warm-up-cron}' : '-'}")
    public void warmUpCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始执行菜单缓存预热任务");
            menuCacheService.warmUpCache();
            log.info("菜单缓存预热任务完成");
        } catch (Exception e) {
            log.error("菜单缓存预热任务失败", e);
        }
    }

    /**
     * 判断菜单是否需要刷新
     *
     * @param menu 菜单对象
     * @return 是否需要刷新
     */
    private boolean shouldRefreshMenu(Menu menu) {
        if (menu == null) {
            return false;
        }

        // 检查菜单是否在时间窗口内有更新
        if (menu.getUpdateTime() != null) {
            LocalDateTime updateTime = menu.getUpdateTime();
            LocalDateTime now = LocalDateTime.now();
            long minutesBetween = ChronoUnit.MINUTES.between(updateTime, now);
            return minutesBetween <= 15;
        }

        return false;
    }
}