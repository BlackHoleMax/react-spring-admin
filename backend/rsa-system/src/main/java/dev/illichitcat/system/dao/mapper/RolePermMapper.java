package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.RolePerm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface RolePermMapper extends BaseMapper<RolePerm> {

    /**
     * 根据角色ID删除角色权限关联
     *
     * @param roleId 角色ID
     * @return 删除记录数
     */
    @Delete("DELETE FROM sys_role_perm WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID删除角色权限关联
     *
     * @param permId 权限ID
     * @return 删除记录数
     */
    @Delete("DELETE FROM sys_role_perm WHERE perm_id = #{permId}")
    int deleteByPermId(@Param("permId") Long permId);

    /**
     * 查询所有不同的角色ID
     *
     * @return 角色ID列表
     */
    @Select("SELECT DISTINCT role_id FROM sys_role_perm")
    List<Long> selectDistinctRoleIds();

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT perm_id FROM sys_role_perm WHERE role_id = #{roleId}")
    List<Long> selectPermIdsByRoleId(@Param("roleId") Long roleId);
}