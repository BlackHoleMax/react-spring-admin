package dev.illichitcat.system.manager;

/**
 * 角色管理器接口
 * 负责封装第三方平台接口，适配上层接口
 * 提供通用业务处理逻辑，如缓存方案、中间件通用处理
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface RoleManager {

    /**
     * 根据用户ID获取角色列表（带缓存）
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    java.util.List<dev.illichitcat.system.model.entity.Role> selectRolesByUserId(Long userId);
}