package dev.illichitcat.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.system.model.entity.Notice;
import dev.illichitcat.system.model.query.MyNoticeQuery;
import dev.illichitcat.system.model.query.NoticeQuery;
import dev.illichitcat.system.model.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告Mapper接口
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 分页查询通知公告列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 通知公告VO分页结果
     */
    IPage<NoticeVO> selectNoticePage(Page<NoticeVO> page, @Param("query") NoticeQuery query);

    /**
     * 查询我的通知列表
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @param query  查询条件
     * @return 通知公告VO分页结果
     */
    IPage<NoticeVO> selectMyNoticePage(Page<NoticeVO> page, @Param("userId") Long userId, @Param("query") MyNoticeQuery query);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long selectUnreadCount(@Param("userId") Long userId);

    /**
     * 批量查询通知已读状态
     *
     * @param noticeIds 通知ID列表
     * @param userId    用户ID
     * @return 已读通知ID列表
     */
    List<Long> selectReadNoticeIds(@Param("noticeIds") List<Long> noticeIds, @Param("userId") Long userId);
}