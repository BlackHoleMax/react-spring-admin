package dev.illichitcat.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
@Schema(description = "用户角色关联表")
public class UserRole {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;
}