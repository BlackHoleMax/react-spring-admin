package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.model.entity.UserRole;
import dev.illichitcat.system.service.RoleCacheService;
import dev.illichitcat.system.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleCacheService roleCacheService;

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        log.info("为用户分配角色: userId={}, roleIds={}", userId, roleIds);
        // 先删除用户原有的角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));

        // 添加新的角色分配
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        // 清除用户角色缓存
        roleCacheService.evictUserRolesCache(userId);

        return true;
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        log.info("删除用户角色关联: userId={}", userId);
        boolean result = userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)) >= 0;
        if (result) {
            // 清除用户角色缓存
            roleCacheService.evictUserRolesCache(userId);
        }
        return result;
    }
}