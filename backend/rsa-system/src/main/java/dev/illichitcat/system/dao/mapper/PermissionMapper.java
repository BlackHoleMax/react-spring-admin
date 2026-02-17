package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.Permission;
import dev.illichitcat.system.model.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询权限列表（包含菜单名称）
     *
     * @param name   名称
     * @param perm   权限
     * @param menuId 菜单Id
     * @return List<PermissionVO>
     */
    List<PermissionVO> selectPermissionListWithMenuName(@Param("name") String name,
                                                        @Param("perm") String perm,
                                                        @Param("menuId") Long menuId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户Id
     * @return List<Permission>
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_perm rp ON p.id = rp.perm_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色Id
     * @return List<Permission>
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_perm rp ON p.id = rp.perm_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限标识列表
     *
     * @param userId 用户Id
     * @return List<String>
     */
    @Select("SELECT DISTINCT p.perm FROM sys_permission p " +
            "INNER JOIN sys_role_perm rp ON p.id = rp.perm_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}