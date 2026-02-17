package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.OperLogMapper;
import dev.illichitcat.system.model.dto.OperLogExcelDTO;
import dev.illichitcat.system.model.entity.OperLog;
import dev.illichitcat.system.service.OperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {

    private static final Integer BUSINESS_TYPE_OTHER = 0;
    private static final Integer BUSINESS_TYPE_INSERT = 1;
    private static final Integer BUSINESS_TYPE_UPDATE = 2;
    private static final Integer BUSINESS_TYPE_DELETE = 3;
    private static final Integer BUSINESS_TYPE_GRANT = 4;
    private static final Integer BUSINESS_TYPE_EXPORT = 5;
    private static final Integer BUSINESS_TYPE_IMPORT = 6;
    private static final Integer BUSINESS_TYPE_CLEAN = 7;

    private static final Integer STATUS_NORMAL = 0;
    private static final Integer STATUS_ABNORMAL = 1;

    @Autowired
    private OperLogMapper operLogMapper;

    @Override
    public IPage<OperLog> selectOperLogList(Page<OperLog> page, OperLog operLog, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OperLog> queryWrapper = buildQueryWrapper(operLog, startTime, endTime);
        return operLogMapper.selectPage(page, queryWrapper);
    }

    @Override
    public OperLog selectOperLogById(Long id) {
        return operLogMapper.selectById(id);
    }

    @Override
    public boolean insertOperLog(OperLog operLog) {
        log.info("记录操作日志: title={}, operName={}", operLog.getTitle(), operLog.getOperName());
        return operLogMapper.insert(operLog) > 0;
    }

    /**
     * 异步保存操作日志
     * 使用线程池异步处理，避免阻塞主业务流程
     *
     * @param operLog 操作日志
     */
    @Override
    @Async("asyncExecutor")
    public void insertOperLogAsync(OperLog operLog) {
        try {
            log.debug("异步保存操作日志: title={}, operName={}", operLog.getTitle(), operLog.getOperName());
            operLogMapper.insert(operLog);
            log.debug("操作日志保存成功: id={}, title={}", operLog.getId(), operLog.getTitle());
        } catch (Exception e) {
            log.error("异步保存操作日志失败: title={}, operName={}", operLog.getTitle(), operLog.getOperName(), e);
        }
    }

    @Override
    public boolean deleteOperLogById(Long id) {
        log.info("删除操作日志: id={}", id);
        return operLogMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteOperLogsByIds(Long[] ids) {
        log.info("批量删除操作日志: ids={}", Arrays.toString(ids));
        return operLogMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public boolean cleanOperLog() {
        log.info("清空操作日志");
        return operLogMapper.delete(new LambdaQueryWrapper<>()) >= 0;
    }

    @Override
    public List<OperLogExcelDTO> exportOperLogs(OperLog operLog, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("导出操作日志: operLog={}, startTime={}, endTime={}", operLog, startTime, endTime);
        LambdaQueryWrapper<OperLog> queryWrapper = buildQueryWrapper(operLog, startTime, endTime);
        List<OperLog> operLogList = operLogMapper.selectList(queryWrapper);

        List<OperLogExcelDTO> dtoList = new ArrayList<>(operLogList.size());
        for (OperLog log : operLogList) {
            OperLogExcelDTO dto = new OperLogExcelDTO();
            BeanUtils.copyProperties(log, dto);
            dto.setBusinessTypeStr(convertBusinessType(log.getBusinessType()));
            dto.setStatusStr(convertStatus(log.getStatus()));
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 转换业务类型为中文描述
     *
     * @param businessType 业务类型
     * @return 中文描述
     */
    private String convertBusinessType(Integer businessType) {
        if (businessType == null) {
            return "其它";
        }
        return switch (businessType) {
            case 1 -> "新增";
            case 2 -> "修改";
            case 3 -> "删除";
            case 4 -> "授权";
            case 5 -> "导出";
            case 6 -> "导入";
            case 7 -> "清空";
            default -> "其它";
        };
    }

    /**
     * 转换状态为中文描述
     *
     * @param status 状态
     * @return 中文描述
     */
    private String convertStatus(Integer status) {
        if (status == null) {
            return "正常";
        }
        return STATUS_NORMAL.equals(status) ? "正常" : "异常";
    }

    /**
     * 构建查询条件
     *
     * @param operLog   操作日志查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 查询包装器
     */
    private LambdaQueryWrapper<OperLog> buildQueryWrapper(OperLog operLog, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OperLog> queryWrapper = new LambdaQueryWrapper<>();
        if (operLog != null) {
            if (operLog.getTitle() != null && !operLog.getTitle().isEmpty()) {
                queryWrapper.like(OperLog::getTitle, operLog.getTitle());
            }
            if (operLog.getBusinessType() != null) {
                queryWrapper.eq(OperLog::getBusinessType, operLog.getBusinessType());
            }
            if (operLog.getOperName() != null && !operLog.getOperName().isEmpty()) {
                queryWrapper.like(OperLog::getOperName, operLog.getOperName());
            }
            if (operLog.getStatus() != null) {
                queryWrapper.eq(OperLog::getStatus, operLog.getStatus());
            }
        }
        if (startTime != null) {
            queryWrapper.ge(OperLog::getOperTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(OperLog::getOperTime, endTime);
        }
        queryWrapper.orderByDesc(OperLog::getOperTime);
        return queryWrapper;
    }
}