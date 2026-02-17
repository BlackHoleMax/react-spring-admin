package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色菜单关联Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色ID删除角色菜单关联
     *
     * @param roleId 角色Id
     * @return int 删除结果
     */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID删除角色菜单关联
     *
     * @param menuId 菜单Id
     * @return int 删除结果
     */
    @Delete("DELETE FROM sys_role_menu WHERE menu_id = #{menuId}")
    int deleteByMenuId(@Param("menuId") Long menuId);

    /**
     * 查询所有不同的角色ID
     *
     * @return 角色ID列表
     */
    @Select("SELECT DISTINCT role_id FROM sys_role_menu")
    List<Long> selectDistinctRoleIds();

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID列表查询菜单ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT menu_id FROM sys_role_menu WHERE role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectMenuIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
