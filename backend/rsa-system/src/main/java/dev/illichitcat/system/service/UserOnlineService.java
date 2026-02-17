package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.UserOnlineDTO;
import dev.illichitcat.system.model.entity.UserOnline;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface UserOnlineService extends IService<UserOnline> {

    /**
     * 分页查询在线用户
     *
     * @param page     分页参数
     * @param username 用户名（模糊查询）
     * @param ip       IP地址（模糊查询）
     * @return 分页结果
     */
    Page<UserOnlineDTO> selectOnlineUserPage(Page<UserOnline> page, String username, String ip);

    /**
     * 根据会话ID踢出用户
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    boolean kickoutBySessionId(String sessionId);

    /**
     * 批量踢出用户
     *
     * @param sessionIds 会话ID列表
     * @return 操作结果
     */
    boolean batchKickout(List<String> sessionIds);

    /**
     * 根据用户ID踢出所有会话
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean kickoutByUserId(Long userId);

    /**
     * 添加用户会话
     *
     * @param userOnline 在线用户信息
     * @return 操作结果
     */
    boolean addOnlineUser(UserOnline userOnline);

    /**
     * 更新用户会话最后访问时间
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    boolean updateLastAccessTime(String sessionId);

    /**
     * 删除过期会话
     *
     * @return 删除数量
     */
    int removeExpiredSessions();

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    long getOnlineUserCount();

    /**
     * 根据会话ID获取在线用户
     *
     * @param sessionId 会话ID
     * @return 在线用户信息
     */
    UserOnlineDTO getBySessionId(String sessionId);

    /**
     * 检查用户是否已在线（单点登录验证）
     *
     * @param userId 用户ID
     * @return 如果已在线返回 true，否则返回 false
     */
    boolean isUserOnline(Long userId);

    /**
     * 验证总在线人数是否超过总用户数
     *
     * @param totalUserCount 总用户数
     * @return 如果超过返回 true，否则返回 false
     */
    boolean isOnlineCountExceedsTotal(long totalUserCount);
}