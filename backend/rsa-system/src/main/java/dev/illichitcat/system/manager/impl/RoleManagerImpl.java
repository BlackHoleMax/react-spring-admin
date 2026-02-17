package dev.illichitcat.system.manager.impl;

import dev.illichitcat.system.dao.mapper.RoleMapper;
import dev.illichitcat.system.manager.RoleManager;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.service.RoleCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色管理器实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Component
@Slf4j
public class RoleManagerImpl implements RoleManager {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleCacheService roleCacheService;

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        log.debug("通过Manager获取用户角色，用户ID: {}", userId);
        // 从缓存获取用户角色列表
        return roleCacheService.getUserRolesFromCache(userId);
    }
}