package dev.illichitcat.system.listener.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.illichitcat.system.config.PermissionCacheProperties;
import dev.illichitcat.system.dao.mapper.PermissionMapper;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.service.PermissionCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限缓存定时任务
 *
 * @author Illichitcat
 * @since 2025/12/25
 */
@Slf4j
@Component
public class PermissionCacheScheduledTask {

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionCacheProperties cacheProperties;

    /**
     * 定时检查权限更新情况，刷新缓存
     */
    @Scheduled(cron = "#{${permission.cache.scheduled.enabled} ? '0 */15 * * * ?' : '-'}")
    public void refreshPermissionCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始定时刷新权限缓存");

            // 获取所有权限
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            List<Permission> permissionList = permissionMapper.selectList(wrapper);
            if (permissionList.isEmpty()) {
                log.info("没有权限数据需要刷新");
                return;
            }

            log.info("定时刷新权限缓存完成, 总权限数={}", permissionList.size());
        } catch (Exception e) {
            log.error("定时刷新权限缓存失败", e);
        }
    }

    /**
     * 每天凌晨4点执行全量缓存预热
     */
    @Scheduled(cron = "#{${permission.cache.scheduled.enabled} ? '${permission.cache.scheduled.warm-up-cron}' : '-'}")
    public void warmUpCache() {
        try {
            if (!cacheProperties.isEnabled() || !cacheProperties.getScheduled().isEnabled()) {
                return;
            }

            log.info("开始执行权限缓存预热任务");
            permissionCacheService.warmUpCache();
            log.info("权限缓存预热任务完成");
        } catch (Exception e) {
            log.error("权限缓存预热任务失败", e);
        }
    }
}