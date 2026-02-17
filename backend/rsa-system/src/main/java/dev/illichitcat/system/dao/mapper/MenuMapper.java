package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户Id
     * @return List<Menu>
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.del_flag = 0 " +
            "ORDER BY m.sort")
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色Id
     * @return List<Menu>
     */
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} AND m.status = 1 AND m.del_flag = 0 " +
            "ORDER BY m.sort")
    List<Menu> selectMenusByRoleId(@Param("roleId") Long roleId);
}
