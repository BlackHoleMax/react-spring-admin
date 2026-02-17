package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.LoginLogMapper;
import dev.illichitcat.system.model.entity.LoginLog;
import dev.illichitcat.system.service.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 登录日志服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public IPage<LoginLog> selectLoginLogList(Page<LoginLog> page, LoginLog loginLog, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<LoginLog> queryWrapper = buildQueryWrapper(loginLog, startTime, endTime);
        return loginLogMapper.selectPage(page, queryWrapper);
    }

    @Override
    public LoginLog selectLoginLogById(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public boolean insertLoginLog(LoginLog loginLog) {
        log.info("记录登录日志: username={}, ip={}", loginLog.getUsername(), loginLog.getIp());
        return loginLogMapper.insert(loginLog) > 0;
    }

    /**
     * 异步保存登录日志
     * 使用线程池异步处理，避免阻塞主业务流程
     *
     * @param loginLog 登录日志
     */
    @Override
    @Async("asyncExecutor")
    public void insertLoginLogAsync(LoginLog loginLog) {
        try {
            log.debug("异步保存登录日志: username={}, ip={}", loginLog.getUsername(), loginLog.getIp());
            loginLogMapper.insert(loginLog);
            log.debug("登录日志保存成功: id={}, username={}", loginLog.getId(), loginLog.getUsername());
        } catch (Exception e) {
            log.error("异步保存登录日志失败: username={}, ip={}", loginLog.getUsername(), loginLog.getIp(), e);
        }
    }

    @Override
    public boolean deleteLoginLogById(Long id) {
        log.info("删除登录日志: id={}", id);
        return loginLogMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteLoginLogsByIds(Long[] ids) {
        log.info("批量删除登录日志: ids={}", Arrays.toString(ids));
        return loginLogMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public boolean cleanLoginLog() {
        log.info("清空登录日志");
        return loginLogMapper.delete(new LambdaQueryWrapper<>()) >= 0;
    }

    @Override
    public long countByUserId(Long userId) {
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLog::getUserId, userId);
        queryWrapper.eq(LoginLog::getStatus, 1);
        return loginLogMapper.selectCount(queryWrapper);
    }

    @Override
    public java.util.List<LoginLog> exportLoginLogList(LoginLog loginLog, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<LoginLog> queryWrapper = buildQueryWrapper(loginLog, startTime, endTime);
        return loginLogMapper.selectList(queryWrapper);
    }

    /**
     * 构建查询条件
     *
     * @param loginLog  查询条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<LoginLog> buildQueryWrapper(LoginLog loginLog, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        if (loginLog != null) {
            if (loginLog.getUsername() != null && !loginLog.getUsername().isEmpty()) {
                queryWrapper.like(LoginLog::getUsername, loginLog.getUsername());
            }
            if (loginLog.getIp() != null && !loginLog.getIp().isEmpty()) {
                queryWrapper.like(LoginLog::getIp, loginLog.getIp());
            }
            if (loginLog.getStatus() != null) {
                queryWrapper.eq(LoginLog::getStatus, loginLog.getStatus());
            }
        }
        if (startTime != null) {
            queryWrapper.ge(LoginLog::getLoginTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(LoginLog::getLoginTime, endTime);
        }
        queryWrapper.orderByDesc(LoginLog::getLoginTime);
        return queryWrapper;
    }
}
