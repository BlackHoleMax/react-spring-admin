package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.RolePerm;

import java.util.List;

/**
 * 角色权限关联服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface RolePermService extends IService<RolePerm> {

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> selectPermIdsByRoleId(Long roleId);

    /**
     * 保存角色权限关联
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @return 是否成功
     */
    boolean saveRolePerms(Long roleId, List<Long> permIds);

    /**
     * 根据角色ID删除角色权限关联
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteByRoleId(Long roleId);

    /**
     * 根据权限ID删除角色权限关联
     *
     * @param permId 权限ID
     * @return 是否成功
     */
    boolean deleteByPermId(Long permId);
}