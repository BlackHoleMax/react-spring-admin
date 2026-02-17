package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.Config;

/**
 * 系统配置服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface ConfigService extends IService<Config> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，如果不存在则返回默认值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取布尔类型的配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    boolean getBooleanValue(String configKey, boolean defaultValue);

    /**
     * 更新配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    boolean updateConfig(String configKey, String configValue);

    /**
     * 更新或插入配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @param configName  配置名称
     * @param remark      备注
     * @return 是否成功
     */
    boolean saveOrUpdateConfig(String configKey, String configValue, String configName, String remark);
}