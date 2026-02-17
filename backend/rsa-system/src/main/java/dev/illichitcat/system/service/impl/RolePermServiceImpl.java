package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.RolePermMapper;
import dev.illichitcat.system.model.entity.RolePerm;
import dev.illichitcat.system.service.RoleCacheService;
import dev.illichitcat.system.service.RolePermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色权限关联服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class RolePermServiceImpl extends ServiceImpl<RolePermMapper, RolePerm> implements RolePermService {

    @Autowired
    private RolePermMapper rolePermMapper;

    @Autowired
    private RoleCacheService roleCacheService;

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Override
    public List<Long> selectPermIdsByRoleId(Long roleId) {
        return rolePermMapper.selectPermIdsByRoleId(roleId);
    }

    /**
     * 保存角色权限关联
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRolePerms(Long roleId, List<Long> permIds) {
        log.info("保存角色权限关联: roleId={}, permIds={}", roleId, permIds);
        // 先删除原有关联
        rolePermMapper.deleteByRoleId(roleId);
        // 批量插入新关联
        if (permIds != null && !permIds.isEmpty()) {
            List<RolePerm> rolePerms = new ArrayList<>();
            for (Long permId : permIds) {
                RolePerm rolePerm = new RolePerm();
                rolePerm.setRoleId(roleId);
                rolePerm.setPermId(permId);
                rolePerms.add(rolePerm);
            }
            boolean result = this.saveBatch(rolePerms);
            if (result) {
                // 保存成功后，缓存角色权限
                roleCacheService.cacheRolePerms(roleId, permIds);
            }
            return result;
        }
        return true;
    }

    /**
     * 根据角色ID删除角色权限关联
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Override
    public boolean deleteByRoleId(Long roleId) {
        log.info("删除角色权限关联: roleId={}", roleId);
        boolean result = rolePermMapper.deleteByRoleId(roleId) >= 0;
        if (result) {
            // 删除成功后，清除缓存
            roleCacheService.evictRolePermsCache(roleId);
        }
        return result;
    }

    /**
     * 根据权限ID删除角色权限关联
     *
     * @param permId 权限ID
     * @return 是否成功
     */
    @Override
    public boolean deleteByPermId(Long permId) {
        log.info("删除角色权限关联: permId={}", permId);
        return rolePermMapper.deleteByPermId(permId) >= 0;
    }
}