package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.illichitcat.system.model.entity.UserOnline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 在线用户Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Mapper
public interface UserOnlineMapper extends BaseMapper<UserOnline> {

    /**
     * 根据会话ID删除在线用户
     *
     * @param sessionId 会话ID
     * @return 删除结果
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID列表批量删除在线用户
     *
     * @param sessionIds 会话ID列表
     * @return 删除结果
     */
    int deleteByIds(@Param("sessionIds") List<String> sessionIds);

    /**
     * 根据用户ID删除在线用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除过期会话
     *
     * @param now 当前时间
     * @return 删除结果
     */
    int deleteExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * 更新最后访问时间
     *
     * @param sessionId 会话ID
     * @param lastTime  最后访问时间
     * @return 更新结果
     */
    int updateLastTime(@Param("sessionId") String sessionId, @Param("lastTime") LocalDateTime lastTime);
}