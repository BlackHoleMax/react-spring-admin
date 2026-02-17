package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.OperLogExcelDTO;
import dev.illichitcat.system.model.entity.OperLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface OperLogService extends IService<OperLog> {

    /**
     * 分页查询操作日志列表
     *
     * @param page      分页对象
     * @param operLog   查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 操作日志分页列表
     */
    IPage<OperLog> selectOperLogList(Page<OperLog> page, OperLog operLog, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID查询操作日志
     *
     * @param id 操作日志ID
     * @return 操作日志信息
     */
    OperLog selectOperLogById(Long id);

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志信息
     * @return 是否成功
     */
    boolean insertOperLog(OperLog operLog);

    /**
     * 异步保存操作日志
     * 使用线程池异步处理，避免阻塞主业务流程
     *
     * @param operLog 操作日志信息
     */
    void insertOperLogAsync(OperLog operLog);

    /**
     * 删除操作日志
     *
     * @param id 操作日志ID
     * @return 是否成功
     */
    boolean deleteOperLogById(Long id);

    /**
     * 批量删除操作日志
     *
     * @param ids 操作日志ID数组
     * @return 是否成功
     */
    boolean deleteOperLogsByIds(Long[] ids);

    /**
     * 清空操作日志
     *
     * @return 是否成功
     */
    boolean cleanOperLog();

    /**
     * 导出操作日志
     *
     * @param operLog   查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 操作日志Excel数据列表
     */
    List<OperLogExcelDTO> exportOperLogs(OperLog operLog, LocalDateTime startTime, LocalDateTime endTime);
}