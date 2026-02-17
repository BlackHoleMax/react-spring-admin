package dev.illichitcat.system.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.utils.JwtUtil;
import dev.illichitcat.system.dao.mapper.UserOnlineMapper;
import dev.illichitcat.system.model.dto.UserOnlineDTO;
import dev.illichitcat.system.model.entity.UserOnline;
import dev.illichitcat.system.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 在线用户服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserOnlineServiceImpl extends ServiceImpl<UserOnlineMapper, UserOnline> implements UserOnlineService {

    private static final String ONLINE_USER_PREFIX = "online:user:";
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public Page<UserOnlineDTO> selectOnlineUserPage(Page<UserOnline> page, String username, String ip) {
        LambdaQueryWrapper<UserOnline> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(username)) {
            wrapper.like(UserOnline::getUsername, username);
        }
        if (StrUtil.isNotBlank(ip)) {
            wrapper.like(UserOnline::getIp, ip);
        }

        wrapper.eq(UserOnline::getStatus, "online");
        wrapper.orderByDesc(UserOnline::getLastTime);

        Page<UserOnline> userOnlinePage = this.page(page, wrapper);

        // 转换为DTO
        Page<UserOnlineDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(userOnlinePage, dtoPage, "records");

        List<UserOnlineDTO> dtoList = new ArrayList<>();
        for (UserOnline userOnline : userOnlinePage.getRecords()) {
            UserOnlineDTO dto = new UserOnlineDTO();
            BeanUtils.copyProperties(userOnline, dto);
            dto.setOnlineMinutes(calculateOnlineMinutes(userOnline.getStartTime()));
            dtoList.add(dto);
        }

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public boolean kickoutBySessionId(String sessionId) {
        try {
            // 从数据库删除
            boolean result = this.removeById(sessionId);

            // 从Redis删除会话信息
            stringRedisTemplate.delete(ONLINE_USER_PREFIX + sessionId);

            // 获取并删除对应的token，让用户强制下线
            String token = stringRedisTemplate.opsForValue().get("session_token:" + sessionId);
            if (token != null) {
                // 通过token获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    // 删除用户的token，让用户强制下线
                    stringRedisTemplate.delete("user_token:" + userId);
                }
                // 删除session-token映射
                stringRedisTemplate.delete("session_token:" + sessionId);
                log.info("踢出会话成功: {}, 已删除对应token", sessionId);
            } else {
                log.info("踢出会话成功: {}, 未找到对应token", sessionId);
            }

            return result;
        } catch (Exception e) {
            log.error("踢出会话失败: {}", sessionId, e);
            return false;
        }
    }

    @Override
    public boolean batchKickout(List<String> sessionIds) {
        try {
            // 批量从数据库删除
            boolean result = this.removeByIds(sessionIds);

            // 批量从Redis删除并让对应token失效
            for (String sessionId : sessionIds) {
                // 删除在线用户Redis记录
                stringRedisTemplate.delete(ONLINE_USER_PREFIX + sessionId);

                // 获取并删除对应的token
                String token = stringRedisTemplate.opsForValue().get("session_token:" + sessionId);
                if (token != null) {
                    try {
                        // 通过token获取用户ID
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        if (userId != null) {
                            // 删除用户的token，让用户强制下线
                            stringRedisTemplate.delete("user_token:" + userId);
                        }
                    } catch (Exception e) {
                        log.warn("解析token失败，sessionId: {}", sessionId, e);
                    }
                    // 删除session-token映射
                    stringRedisTemplate.delete("session_token:" + sessionId);
                }
            }

            log.info("批量踢出会话成功: {}", sessionIds);
            return result;
        } catch (Exception e) {
            log.error("批量踢出会话失败: {}", sessionIds, e);
            return false;
        }
    }

    @Override
    public boolean kickoutByUserId(Long userId) {
        try {
            // 直接删除用户的token，让用户强制下线
            stringRedisTemplate.delete("user_token:" + userId);

            LambdaQueryWrapper<UserOnline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserOnline::getUserId, userId);

            List<UserOnline> onlineUsers = this.list(wrapper);
            if (onlineUsers.isEmpty()) {
                log.info("用户 {} 没有在线会话", userId);
                return true;
            }

            List<String> sessionIds = onlineUsers.stream()
                    .map(UserOnline::getId)
                    .toList();

            // 删除所有相关的session-token映射
            for (String sessionId : sessionIds) {
                stringRedisTemplate.delete("session_token:" + sessionId);
                stringRedisTemplate.delete(ONLINE_USER_PREFIX + sessionId);
            }

            // 从数据库删除
            boolean result = this.removeByIds(sessionIds);

            log.info("根据用户ID踢出会话成功: {}, 会话数: {}", userId, sessionIds.size());
            return result;
        } catch (Exception e) {
            log.error("根据用户ID踢出会话失败: {}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOnlineUser(UserOnline userOnline) {
        try {
            log.info("开始添加在线用户: {}, IP: {}, Token: {}", userOnline.getUsername(), userOnline.getIp(), userOnline.getId());

            // 检查该用户是否已有在线会话（单点登录）
            // 使用数据库的唯一约束或原子操作来避免竞态条件
            LambdaQueryWrapper<UserOnline> existingWrapper = new LambdaQueryWrapper<>();
            existingWrapper.eq(UserOnline::getUserId, userOnline.getUserId());
            existingWrapper.eq(UserOnline::getStatus, "online");

            List<UserOnline> existingSessions = this.list(existingWrapper);
            if (!existingSessions.isEmpty()) {
                // 自动踢出旧会话（单点登录）
                log.warn("用户 {} 已有在线会话，自动踢出旧会话，会话数: {}",
                        userOnline.getUsername(), existingSessions.size());

                List<String> oldSessionIds = existingSessions.stream()
                        .map(UserOnline::getId)
                        .toList();

                // 批量删除旧会话的 Redis 记录
                for (String oldSessionId : oldSessionIds) {
                    stringRedisTemplate.delete(ONLINE_USER_PREFIX + oldSessionId);
                    String oldToken = stringRedisTemplate.opsForValue().get("session_token:" + oldSessionId);
                    if (oldToken != null) {
                        // 删除旧会话的 token
                        stringRedisTemplate.delete("user_token:" + userOnline.getUserId());
                        stringRedisTemplate.delete("session_token:" + oldSessionId);
                    }
                }

                // 从数据库删除旧会话
                this.removeByIds(oldSessionIds);
                log.info("已自动踢出用户 {} 的 {} 个旧会话", userOnline.getUsername(), oldSessionIds.size());
            }

            // 保存到数据库
            boolean result = this.save(userOnline);

            // 保存到Redis，设置过期时间
            String redisKey = ONLINE_USER_PREFIX + userOnline.getId();
            // 默认24小时过期
            stringRedisTemplate.opsForValue().set(redisKey, userOnline.getUserId().toString(),
                    24, TimeUnit.HOURS);

            log.info("添加在线用户成功: {}, IP: {}, 结果: {}", userOnline.getUsername(), userOnline.getIp(), result);
            return result;
        } catch (Exception e) {
            log.error("添加在线用户失败: {}", userOnline.getUsername(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLastAccessTime(String sessionId) {
        try {
            // 更新数据库
            baseMapper.updateLastTime(sessionId, LocalDateTime.now());

            // 更新Redis过期时间
            String redisKey = ONLINE_USER_PREFIX + sessionId;
            stringRedisTemplate.expire(redisKey, 24, TimeUnit.HOURS);

            return true;
        } catch (Exception e) {
            log.error("更新最后访问时间失败: {}", sessionId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeExpiredSessions() {
        try {
            // 1. 清理过期的会话
            int expiredCount = baseMapper.deleteExpiredSessions(LocalDateTime.now());

            // 2. 清理无效的会话（token已失效）
            int invalidCount = removeInvalidSessions();

            int totalCount = expiredCount + invalidCount;
            log.info("清理会话完成 - 过期会话: {} 个, 无效会话: {} 个, 总计: {} 个",
                    expiredCount, invalidCount, totalCount);
            return totalCount;
        } catch (Exception e) {
            log.error("清理会话失败", e);
            return 0;
        }
    }

    /**
     * 清理无效的会话（token已失效）
     *
     * @return 清理的会话数量
     */
    private int removeInvalidSessions() {
        try {
            // 获取所有在线会话
            LambdaQueryWrapper<UserOnline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserOnline::getStatus, "online");
            List<UserOnline> onlineSessions = this.list(wrapper);

            int invalidCount = 0;
            List<String> invalidSessionIds = new ArrayList<>();

            for (UserOnline session : onlineSessions) {
                try {
                    // 检查对应的token是否有效
                    String token = stringRedisTemplate.opsForValue().get("session_token:" + session.getId());

                    // 如果 session_token 映射丢失，尝试从 user_token 恢复
                    if (token == null) {
                        String userToken = stringRedisTemplate.opsForValue().get("user_token:" + session.getUserId());
                        if (userToken != null && jwtUtil.validateToken(userToken)) {
                            // 验证这个token是否属于当前会话
                            Long tokenUserId = jwtUtil.getUserIdFromToken(userToken);
                            if (tokenUserId != null && tokenUserId.equals(session.getUserId())) {
                                // 恢复 session_token 映射
                                stringRedisTemplate.opsForValue().set("session_token:" + session.getId(), userToken, 24, TimeUnit.HOURS);
                                log.debug("恢复 session_token 映射: sessionId={}, userId={}", session.getId(), session.getUserId());
                                continue; // 跳过这个会话，不删除
                            }
                        }
                    }

                    // 如果 token 存在但验证失败，标记为无效
                    if (token != null && !jwtUtil.validateToken(token)) {
                        invalidSessionIds.add(session.getId());
                    }
                } catch (Exception e) {
                    log.warn("检查会话失败: sessionId={}, userId={}", session.getId(), session.getUserId(), e);
                    // 检查失败时，为了安全起见，标记为无效
                    invalidSessionIds.add(session.getId());
                }
            }

            // 批量删除无效会话
            if (!invalidSessionIds.isEmpty()) {
                // 从数据库删除
                this.removeByIds(invalidSessionIds);

                // 从Redis删除相关记录
                for (String sessionId : invalidSessionIds) {
                    stringRedisTemplate.delete(ONLINE_USER_PREFIX + sessionId);
                    stringRedisTemplate.delete("session_token:" + sessionId);
                }

                invalidCount = invalidSessionIds.size();
                log.info("清理无效会话: {} 个, 会话IDs: {}", invalidCount, invalidSessionIds);
            }

            return invalidCount;
        } catch (Exception e) {
            log.error("清理无效会话失败", e);
            return 0;
        }
    }

    @Override
    public long getOnlineUserCount() {
        LambdaQueryWrapper<UserOnline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOnline::getStatus, "online");
        long count = this.count(wrapper);
        log.info("当前在线用户数量: {}", count);
        return count;
    }

    @Override
    public UserOnlineDTO getBySessionId(String sessionId) {
        UserOnline userOnline = this.getById(sessionId);
        if (userOnline == null) {
            return null;
        }

        UserOnlineDTO dto = new UserOnlineDTO();
        BeanUtils.copyProperties(userOnline, dto);
        dto.setOnlineMinutes(calculateOnlineMinutes(userOnline.getStartTime()));
        return dto;
    }

    @Override
    public boolean isUserOnline(Long userId) {
        LambdaQueryWrapper<UserOnline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOnline::getUserId, userId);
        wrapper.eq(UserOnline::getStatus, "online");
        long count = this.count(wrapper);
        log.info("检查用户在线状态，用户ID: {}, 在线: {}", userId, count > 0);
        return count > 0;
    }

    @Override
    public boolean isOnlineCountExceedsTotal(long totalUserCount) {
        long onlineCount = getOnlineUserCount();
        log.info("检查在线人数，在线人数: {}, 总用户数: {}", onlineCount, totalUserCount);
        return onlineCount >= totalUserCount;
    }

    /**
     * 计算在线时长（分钟）
     *
     * @param startTime 开始时间
     * @return 在线时长（分钟）
     */
    private long calculateOnlineMinutes(LocalDateTime startTime) {
        if (startTime == null) {
            return 0;
        }
        return DateUtil.between(
                DateUtil.date(startTime),
                DateUtil.date(LocalDateTime.now()),
                DateUnit.MINUTE
        );
    }
}