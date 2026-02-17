package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.RoleExcelDTO;
import dev.illichitcat.system.model.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     *
     * @param page 分页对象
     * @param role 查询条件
     * @return 角色分页列表
     */
    IPage<Role> selectRoleList(Page<Role> page, Role role);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    Role selectRoleById(Long id);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean insertRole(Role role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(Role role);

    /**
     * 根据ID删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean deleteRoleById(Long id);

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 导出角色数据到Excel
     *
     * @param roleIds 角色ID列表，为空则导出所有
     * @return 角色Excel数据列表
     */
    List<RoleExcelDTO> exportRoles(List<Long> roleIds);

    /**
     * 从Excel导入角色数据
     *
     * @param roleExcelDTOList 角色Excel数据列表
     * @return 导入结果信息
     */
    String importRoles(List<RoleExcelDTO> roleExcelDTOList);

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID数组
     * @return 删除结果
     */
    boolean deleteRolesByIds(Long[] roleIds);
}