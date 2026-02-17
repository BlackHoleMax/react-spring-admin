package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.UserExcelDTO;
import dev.illichitcat.system.model.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param page 分页对象
     * @param user 查询条件
     * @return 用户分页列表
     */
    IPage<User> selectUserList(IPage<User> page, User user);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectUserByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectUserById(Long userId);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean insertUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUserById(Long userId);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID数组
     * @return 是否成功
     */
    boolean deleteUsersByIds(Long[] userIds);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 新密码
     */
    String resetPassword(Long userId);

    /**
     * 修改用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String newPassword);

    /**
     * 导出用户数据到Excel
     *
     * @param userIds 用户ID列表，为空则导出所有
     * @return 用户Excel数据列表
     */
    List<UserExcelDTO> exportUsers(List<Long> userIds);

    /**
     * 从Excel导入用户数据
     *
     * @param userExcelDTOList 用户Excel数据列表
     * @return 导入结果信息
     */
    String importUsers(List<UserExcelDTO> userExcelDTOList);

    /**
     * 更新用户头像
     *
     * @param userId    用户ID
     * @param avatarUrl 头像URL
     * @return 是否成功
     */
    boolean updateAvatar(Long userId, String avatarUrl);

    /**
     * 验证用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 是否正确
     */
    boolean validatePassword(Long userId, String password);

    /**
     * 更新用户个人资料
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param email    邮箱
     * @param phone    手机号
     * @param gender   性别
     * @return 是否成功
     */
    boolean updateProfile(Long userId, String nickname, String email, String phone, Integer gender);

    /**
     * 获取总用户数
     *
     * @return 总用户数
     */
    long getTotalUserCount();
}