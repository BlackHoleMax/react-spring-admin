package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.LoginLog;

import java.time.LocalDateTime;

/**
 * 登录日志服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 分页查询登录日志列表
     *
     * @param page      分页对象
     * @param loginLog  查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 登录日志分页列表
     */
    IPage<LoginLog> selectLoginLogList(Page<LoginLog> page, LoginLog loginLog, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID查询登录日志
     *
     * @param id 登录日志ID
     * @return 登录日志信息
     */
    LoginLog selectLoginLogById(Long id);

    /**
     * 新增登录日志
     *
     * @param loginLog 登录日志信息
     * @return 是否成功
     */
    boolean insertLoginLog(LoginLog loginLog);

    /**
     * 异步保存登录日志
     * 使用线程池异步处理，避免阻塞主业务流程
     *
     * @param loginLog 登录日志信息
     */
    void insertLoginLogAsync(LoginLog loginLog);

    /**
     * 删除登录日志
     *
     * @param id 登录日志ID
     * @return 是否成功
     */
    boolean deleteLoginLogById(Long id);

    /**
     * 批量删除登录日志
     *
     * @param ids 登录日志ID数组
     * @return 是否成功
     */
    boolean deleteLoginLogsByIds(Long[] ids);

    /**
     * 清空登录日志
     *
     * @return 是否成功
     */
    boolean cleanLoginLog();

    /**
     * 统计用户登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    long countByUserId(Long userId);

    /**
     * 导出登录日志列表
     *
     * @param loginLog  查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 登录日志列表
     */
    java.util.List<dev.illichitcat.system.model.entity.LoginLog> exportLoginLogList(
            dev.illichitcat.system.model.entity.LoginLog loginLog,
            java.time.LocalDateTime startTime,
            java.time.LocalDateTime endTime
    );
}