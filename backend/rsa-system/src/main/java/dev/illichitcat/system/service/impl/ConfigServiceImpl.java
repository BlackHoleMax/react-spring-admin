package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.ConfigMapper;
import dev.illichitcat.system.model.entity.Config;
import dev.illichitcat.system.service.ConfigCacheService;
import dev.illichitcat.system.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Autowired
    @Lazy
    private ConfigCacheService configCacheService;

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = configCacheService.getConfigValueFromCache(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean getBooleanValue(String configKey, boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey)
                .eq(Config::getDelFlag, 0);

        Config config = getOne(wrapper);
        if (config != null) {
            config.setConfigValue(configValue);
            boolean result = updateById(config);
            if (result) {
                // 清除配置缓存
                configCacheService.evictConfigCache(configKey);
            }
            return result;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateConfig(String configKey, String configValue, String configName, String remark) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey)
                .eq(Config::getDelFlag, 0);

        Config config = getOne(wrapper);
        if (config != null) {
            config.setConfigValue(configValue);
            if (configName != null) {
                config.setConfigName(configName);
            }
            if (remark != null) {
                config.setRemark(remark);
            }
            boolean result = updateById(config);
            if (result) {
                // 清除配置缓存
                configCacheService.evictConfigCache(configKey);
            }
            return result;
        } else {
            config = new Config();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setConfigName(configName);
            config.setRemark(remark);
            config.setStatus(1);
            config.setDelFlag(0);
            boolean result = save(config);
            if (result) {
                // 清除配置缓存
                configCacheService.evictConfigCache(configKey);
            }
            return result;
        }
    }
}