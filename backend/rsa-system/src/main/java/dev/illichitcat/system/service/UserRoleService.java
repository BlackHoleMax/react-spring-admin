package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 根据用户ID获取角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 保存用户角色关联（别名方法）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    default boolean saveUserRoles(Long userId, List<Long> roleIds) {
        return assignRolesToUser(userId, roleIds);
    }

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteByUserId(Long userId);
}