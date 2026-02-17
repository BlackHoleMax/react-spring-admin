package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.illichitcat.system.config.NoticeWebSocketHandler;
import dev.illichitcat.system.dao.mapper.NoticeMapper;
import dev.illichitcat.system.dao.mapper.NoticeReadMapper;
import dev.illichitcat.system.dao.mapper.UserMapper;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.model.dto.NoticeDTO;
import dev.illichitcat.system.model.entity.Notice;
import dev.illichitcat.system.model.entity.NoticeRead;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.model.query.MyNoticeQuery;
import dev.illichitcat.system.model.query.NoticeQuery;
import dev.illichitcat.system.model.vo.NoticeVO;
import dev.illichitcat.system.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知公告服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Service
@Slf4j
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    /**
     * 公告状态 - 草稿
     */
    private static final Integer STATUS_DRAFT = 1;

    /**
     * 公告状态 - 已发布
     */
    private static final Integer STATUS_PUBLISHED = 2;

    /**
     * 公告状态 - 已撤回
     */
    private static final Integer STATUS_REVOKED = 3;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private NoticeReadMapper noticeReadMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private NoticeWebSocketHandler noticeWebSocketHandler;

    @Override
    public IPage<NoticeVO> selectNoticePage(IPage<NoticeVO> page, NoticeQuery query) {
        return noticeMapper.selectNoticePage((Page<NoticeVO>) page, query);
    }

    @Override
    public NoticeVO selectNoticeById(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            return null;
        }
        NoticeVO noticeVO = new NoticeVO();
        BeanUtils.copyProperties(notice, noticeVO);
        noticeVO.setTypeName(getTypeName(notice.getType()));
        noticeVO.setTargetTypeName(getTargetTypeName(notice.getTargetType()));
        noticeVO.setPriorityName(getPriorityName(notice.getPriority()));
        noticeVO.setStatusName(getStatusName(notice.getStatus()));
        if (notice.getTotalCount() != null && notice.getTotalCount() > 0) {
            double rate = (double) notice.getReadCount() / notice.getTotalCount() * 100;
            noticeVO.setReadRate(String.format("%.2f%%", rate));
        } else {
            noticeVO.setReadRate("0.00%");
        }
        return noticeVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        notice.setStatus(1);
        notice.setReadCount(0);
        notice.setTotalCount(0);
        if (noticeDTO.getTargetRoles() != null && !noticeDTO.getTargetRoles().isEmpty()) {
            try {
                notice.setTargetRoles(objectMapper.writeValueAsString(noticeDTO.getTargetRoles()));
            } catch (Exception e) {
                log.error("序列化角色列表失败", e);
                throw new RuntimeException("序列化角色列表失败");
            }
        }
        return this.save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotice(NoticeDTO noticeDTO) {
        Notice notice = this.getById(noticeDTO.getId());
        if (notice == null) {
            return false;
        }
        if (notice.getStatus().equals(STATUS_PUBLISHED)) {
            throw new RuntimeException("已发布的公告不能修改");
        }
        BeanUtils.copyProperties(noticeDTO, notice);
        if (noticeDTO.getTargetRoles() != null && !noticeDTO.getTargetRoles().isEmpty()) {
            try {
                notice.setTargetRoles(objectMapper.writeValueAsString(noticeDTO.getTargetRoles()));
            } catch (Exception e) {
                log.error("序列化角色列表失败", e);
                throw new RuntimeException("序列化角色列表失败");
            }
        }
        return this.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNoticeById(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNoticeByIds(Long[] ids) {
        return this.removeBatchByIds(List.of(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishNotice(Long id, Long userId, String username) {
        Notice notice = this.getById(id);
        if (notice == null) {
            return false;
        }
        if (notice.getStatus() != 1) {
            throw new RuntimeException("只有草稿状态的公告才能发布");
        }
        notice.setStatus(2);
        notice.setPublishTime(LocalDateTime.now());
        notice.setPublisherId(userId);
        notice.setPublisherName(username);
        int totalCount = calculateTargetCount(notice);
        notice.setTotalCount(totalCount);
        boolean result = this.updateById(notice);
        if (result) {
            log.info("通知发布成功，开始异步推送通知，通知ID: {}, 标题: {}, 目标类型: {}, 目标角色: {}, 总目标用户数: {}",
                    notice.getId(), notice.getTitle(), notice.getTargetType(), notice.getTargetRoles(), totalCount);
            sendNoticeViaWebSocketAsync(notice);
            log.info("通知推送请求已提交，通知ID: {}", notice.getId());
        }
        return result;
    }

    /**
     * 异步推送通知
     * 使用线程池异步处理，避免阻塞发布流程
     *
     * @param notice 通知信息
     */
    @Override
    @Async("asyncExecutor")
    public void sendNoticeViaWebSocketAsync(Notice notice) {
        try {
            List<Long> targetUserIds = getTargetUserIds(notice);
            log.info("异步推送通知开始，通知ID: {}, 标题: {}, 目标用户数: {}",
                    notice.getId(), notice.getTitle(), targetUserIds.size());

            int successCount = 0;
            int offlineCount = 0;

            for (Long userId : targetUserIds) {
                boolean isOnline = noticeWebSocketHandler.isUserOnline(userId);
                log.debug("检查用户在线状态，用户ID: {}, 在线: {}", userId, isOnline);

                if (isOnline) {
                    NoticeVO noticeVO = new NoticeVO();
                    BeanUtils.copyProperties(notice, noticeVO);
                    noticeVO.setTypeName(getTypeName(notice.getType()));
                    noticeVO.setPriorityName(getPriorityName(notice.getPriority()));
                    // 设置为未读状态（0=未读），这样前端才会显示提示
                    noticeVO.setReadStatus(0);
                    noticeWebSocketHandler.sendNoticeToUser(userId, noticeVO);
                    successCount++;
                    log.debug("通知已推送给用户: {}, 通知ID: {}", userId, notice.getId());
                } else {
                    offlineCount++;
                    log.debug("用户 {} 离线，跳过推送", userId);
                }
            }

            log.info("异步推送通知完成，通知ID: {}, 在线用户: {}, 离线用户: {}",
                    notice.getId(), successCount, offlineCount);
        } catch (Exception e) {
            log.error("异步推送通知失败，通知ID: {}, 标题: {}", notice.getId(), notice.getTitle(), e);
        }
    }

    private List<Long> getTargetUserIds(Notice notice) {
        List<Long> userIds;
        if (notice.getTargetType() == 1) {
            QueryWrapper<User> queryWrapper = buildUserQueryWrapper(null);
            queryWrapper.select("id");
            userIds = userMapper.selectList(queryWrapper).stream().map(User::getId).toList();
            log.info("获取全部用户列表，用户数: {}, 用户ID列表: {}", userIds.size(), userIds);
        } else {
            List<Long> roleIds = parseRoleIds(notice.getTargetRoles());
            log.info("获取指定角色用户，角色ID列表: {}", roleIds);
            if (roleIds == null || roleIds.isEmpty()) {
                log.warn("角色ID列表为空，没有目标用户");
                return new ArrayList<>();
            }
            QueryWrapper<User> queryWrapper = buildUserQueryWrapper(roleIds);
            queryWrapper.select("id");
            userIds = userMapper.selectList(queryWrapper).stream().map(User::getId).toList();
            log.info("获取指定角色用户列表，用户数: {}, 用户ID列表: {}", userIds.size(), userIds);
        }
        return userIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            return false;
        }
        if (!notice.getStatus().equals(STATUS_PUBLISHED)) {
            throw new RuntimeException("只有已发布的公告才能撤回");
        }
        notice.setStatus(STATUS_REVOKED);
        return this.updateById(notice);
    }

    @Override
    public IPage<NoticeVO> selectMyNoticePage(IPage<NoticeVO> page, Long userId, MyNoticeQuery query) {
        return noticeMapper.selectMyNoticePage((Page<NoticeVO>) page, userId, query);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return noticeMapper.selectUnreadCount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long noticeId, Long userId) {
        QueryWrapper<NoticeRead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notice_id", noticeId);
        queryWrapper.eq("user_id", userId);
        NoticeRead exist = noticeReadMapper.selectOne(queryWrapper);
        if (exist != null) {
            return true;
        }
        NoticeRead noticeRead = new NoticeRead();
        noticeRead.setNoticeId(noticeId);
        noticeRead.setUserId(userId);
        User user = userMapper.selectById(userId);
        if (user != null) {
            noticeRead.setUsername(user.getUsername());
            noticeRead.setNickname(user.getNickname());
        }
        boolean result = noticeReadMapper.insert(noticeRead) > 0;
        if (result) {
            updateReadCount(noticeId);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkAsRead(Long[] noticeIds, Long userId) {
        for (Long noticeId : noticeIds) {
            markAsRead(noticeId, userId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        QueryWrapper<Notice> noticeQuery = new QueryWrapper<>();
        noticeQuery.eq("status", 2);
        noticeQuery.eq("del_flag", 0);
        List<Notice> notices = this.list(noticeQuery);
        for (Notice notice : notices) {
            markAsRead(notice.getId(), userId);
        }
        return true;
    }

    private int calculateTargetCount(Notice notice) {
        if (notice.getTargetType() == 1) {
            QueryWrapper<User> queryWrapper = buildUserQueryWrapper(null);
            return Math.toIntExact(userMapper.selectCount(queryWrapper));
        } else {
            List<Long> roleIds = parseRoleIds(notice.getTargetRoles());
            if (roleIds == null || roleIds.isEmpty()) {
                return 0;
            }
            QueryWrapper<User> queryWrapper = buildUserQueryWrapper(roleIds);
            return Math.toIntExact(userMapper.selectCount(queryWrapper));
        }
    }

    private void updateReadCount(Long noticeId) {
        QueryWrapper<NoticeRead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notice_id", noticeId);
        long count = noticeReadMapper.selectCount(queryWrapper);
        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setReadCount(Math.toIntExact(count));
        this.updateById(notice);
    }

    private List<Long> parseRoleIds(String targetRoles) {
        if (targetRoles == null || targetRoles.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(targetRoles, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("解析角色列表失败", e);
            return new ArrayList<>();
        }
    }

    private String getTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case 1 -> "系统公告";
            case 2 -> "活动通知";
            default -> "";
        };
    }

    private String getTargetTypeName(Integer targetType) {
        if (targetType == null) {
            return "";
        }
        return switch (targetType) {
            case 1 -> "全部用户";
            case 2 -> "指定角色";
            default -> "";
        };
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) {
            return "";
        }
        return switch (priority) {
            case 1 -> "普通";
            case 2 -> "重要";
            case 3 -> "紧急";
            default -> "";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 1 -> "草稿";
            case 2 -> "已发布";
            case 3 -> "已撤回";
            default -> "";
        };
    }

    /**
     * 构建用户查询条件
     *
     * @param roleIds 角色ID列表（null表示查询全部用户）
     * @return QueryWrapper
     */
    private QueryWrapper<User> buildUserQueryWrapper(List<Long> roleIds) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("del_flag", 0);
        if (roleIds != null && !roleIds.isEmpty()) {
            queryWrapper.inSql("id", "SELECT user_id FROM sys_user_role WHERE role_id IN (" +
                    roleIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("") + ")");
        }
        return queryWrapper;
    }
}