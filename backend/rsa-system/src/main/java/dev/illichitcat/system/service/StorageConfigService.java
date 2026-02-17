package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.StorageConfig;
import dev.illichitcat.system.model.query.StorageConfigQuery;
import dev.illichitcat.system.model.vo.StorageConfigVO;

/**
 * 存储配置服务接口
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
public interface StorageConfigService extends IService<StorageConfig> {

    /**
     * 分页查询存储配置列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 存储配置分页列表
     */
    IPage<StorageConfigVO> selectConfigPage(IPage<StorageConfigVO> page, StorageConfigQuery query);

    /**
     * 根据ID获取存储配置详情
     *
     * @param id 配置ID
     * @return 存储配置详情
     */
    StorageConfigVO getConfigById(Long id);

    /**
     * 新增存储配置
     *
     * @param config 存储配置
     * @return 是否成功
     */
    boolean saveConfig(StorageConfig config);

    /**
     * 更新存储配置
     *
     * @param config 存储配置
     * @return 是否成功
     */
    boolean updateConfig(StorageConfig config);

    /**
     * 删除存储配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long id);

    /**
     * 设置默认配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean setDefaultConfig(Long id);

    /**
     * 获取默认配置
     *
     * @return 默认配置
     */
    StorageConfigVO getDefaultConfig();
}