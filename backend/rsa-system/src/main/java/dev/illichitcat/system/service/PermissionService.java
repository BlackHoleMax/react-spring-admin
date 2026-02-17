package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.PermissionExcelDTO;
import dev.illichitcat.system.model.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 查询所有权限列表
     *
     * @param permission 查询条件
     * @return 权限列表
     */
    List<Permission> selectPermissionList(Permission permission);

    /**
     * 查询权限列表（包含菜单名称）
     *
     * @param permission 查询条件
     * @return 权限列表
     */
    List<Permission> selectPermissionListWithMenuName(Permission permission);

    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Permission selectPermissionById(Long id);

    /**
     * 新增权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean insertPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean deletePermissionById(Long id);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectPermsByUserId(Long userId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByUserId(Long userId);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByRoleId(Long roleId);

    /**
     * 导出权限数据到Excel
     *
     * @param permIds 权限ID列表，为空则导出所有
     * @return 权限Excel数据列表
     */
    List<PermissionExcelDTO> exportPermissions(List<Long> permIds);

    /**
     * 从Excel导入权限数据
     *
     * @param permissionExcelDTOList 权限Excel数据列表
     * @return 导入结果信息
     */
    String importPermissions(List<PermissionExcelDTO> permissionExcelDTOList);

    /**
     * 批量删除权限
     *
     * @param permIds 权限ID数组
     * @return 删除结果
     */
    boolean deletePermissionsByIds(Long[] permIds);
}