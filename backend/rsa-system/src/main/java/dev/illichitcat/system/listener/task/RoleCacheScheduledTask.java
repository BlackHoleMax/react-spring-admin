package dev.illichitcat.system.listener.task;

import dev.illichitcat.system.config.RoleCacheProperties;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.service.RoleCacheService;
import dev.illichitcat.system.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 角色缓存定时任务
 *
 * @author Illichitcat
 * @since 2026/01/10
 */
@Slf4j
@Component
public class RoleCacheScheduledTask {

    @Autowired
    private RoleCacheService roleCacheService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleCacheProperties cacheProperties;

    /**
     * 定时检查角色更新情况，刷新缓存
     */
    @Scheduled(cron = "#{${role.cache.scheduled.enabled} ? '0 */15 * * * ?' : '-'}")
    public void refreshRoleCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始定时刷新角色缓存");

            // 获取所有角色
            List<Role> roleList = roleService.list();
            if (roleList.isEmpty()) {
                log.info("没有角色数据需要刷新");
                return;
            }

            int refreshedCount = 0;
            for (Role role : roleList) {
                try {
                    // 检查角色是否需要刷新
                    if (shouldRefreshRole(role)) {
                        // 清除缓存，下次访问时会重新加载
                        roleCacheService.evictRoleCache(role.getId());
                        if (role.getCode() != null) {
                            roleCacheService.evictRoleCache(role.getCode());
                        }
                        refreshedCount++;

                        log.debug("刷新角色缓存, roleId={}, roleCode={}", role.getId(), role.getCode());
                    }
                } catch (Exception e) {
                    log.error("刷新角色缓存失败, roleId={}, roleCode={}", role.getId(), role.getCode(), e);
                }
            }

            log.info("定时刷新角色缓存完成, 总角色数={}, 刷新数={}", roleList.size(), refreshedCount);
        } catch (Exception e) {
            log.error("定时刷新角色缓存失败", e);
        }
    }

    /**
     * 每天凌晨3点执行全量缓存预热
     */
    @Scheduled(cron = "#{${role.cache.scheduled.enabled} ? '${role.cache.scheduled.warm-up-cron}' : '-'}")
    public void warmUpCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始执行角色缓存预热任务");
            roleCacheService.warmUpCache();
            log.info("角色缓存预热任务完成");
        } catch (Exception e) {
            log.error("角色缓存预热任务失败", e);
        }
    }

    /**
     * 判断角色是否需要刷新
     *
     * @param role 角色对象
     * @return 是否需要刷新
     */
    private boolean shouldRefreshRole(Role role) {
        if (role == null) {
            return false;
        }

        // 检查角色是否在时间窗口内有更新
        if (role.getUpdateTime() != null) {
            LocalDateTime updateTime = role.getUpdateTime();
            LocalDateTime now = LocalDateTime.now();
            long minutesBetween = ChronoUnit.MINUTES.between(updateTime, now);
            return minutesBetween <= 15;
        }

        return false;
    }
}