package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.NoticeDTO;
import dev.illichitcat.system.model.entity.Notice;
import dev.illichitcat.system.model.query.MyNoticeQuery;
import dev.illichitcat.system.model.query.NoticeQuery;
import dev.illichitcat.system.model.vo.NoticeVO;

/**
 * 通知公告服务接口
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询通知公告列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 通知公告分页列表
     */
    IPage<NoticeVO> selectNoticePage(IPage<NoticeVO> page, NoticeQuery query);

    /**
     * 根据ID查询通知公告详情
     *
     * @param id 通知ID
     * @return 通知公告详情
     */
    NoticeVO selectNoticeById(Long id);

    /**
     * 新增通知公告
     *
     * @param noticeDTO 通知公告信息
     * @return 是否成功
     */
    boolean insertNotice(NoticeDTO noticeDTO);

    /**
     * 更新通知公告
     *
     * @param noticeDTO 通知公告信息
     * @return 是否成功
     */
    boolean updateNotice(NoticeDTO noticeDTO);

    /**
     * 删除通知公告
     *
     * @param id 通知ID
     * @return 是否成功
     */
    boolean deleteNoticeById(Long id);

    /**
     * 批量删除通知公告
     *
     * @param ids 通知ID数组
     * @return 是否成功
     */
    boolean deleteNoticeByIds(Long[] ids);

    /**
     * 发布通知公告
     *
     * @param id       通知ID
     * @param userId   发布者ID
     * @param username 发布者用户名
     * @return 是否成功
     */
    boolean publishNotice(Long id, Long userId, String username);

    /**
     * 撤回通知公告
     *
     * @param id 通知ID
     * @return 是否成功
     */
    boolean revokeNotice(Long id);

    /**
     * 查询我的通知列表
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @param query  查询条件
     * @return 我的通知分页列表
     */
    IPage<NoticeVO> selectMyNoticePage(IPage<NoticeVO> page, Long userId, MyNoticeQuery query);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     *
     * @param noticeId 通知ID
     * @param userId   用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long noticeId, Long userId);

    /**
     * 批量标记通知为已读
     *
     * @param noticeIds 通知ID列表
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean batchMarkAsRead(Long[] noticeIds, Long userId);

    /**
     * 全部标记为已读
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);

    /**
     * 异步推送通知
     * 使用线程池异步处理，避免阻塞发布流程
     *
     * @param notice 通知信息
     */
    void sendNoticeViaWebSocketAsync(Notice notice);
}