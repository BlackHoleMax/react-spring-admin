package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.StorageConfigMapper;
import dev.illichitcat.system.model.entity.StorageConfig;
import dev.illichitcat.system.model.query.StorageConfigQuery;
import dev.illichitcat.system.model.vo.StorageConfigVO;
import dev.illichitcat.system.service.StorageConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 存储配置服务实现类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Slf4j
@Service
public class StorageConfigServiceImpl extends ServiceImpl<StorageConfigMapper, StorageConfig> implements StorageConfigService {

    /**
     * 是否默认 - 是
     */
    private static final Integer IS_DEFAULT_YES = 1;

    /**
     * 是否默认 - 否
     */
    private static final Integer IS_DEFAULT_NO = 0;

    @Override
    public IPage<StorageConfigVO> selectConfigPage(IPage<StorageConfigVO> page, StorageConfigQuery query) {
        LambdaQueryWrapper<StorageConfig> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            wrapper.like(query.getConfigKey() != null, StorageConfig::getConfigKey, query.getConfigKey())
                    .eq(query.getStorageProvider() != null, StorageConfig::getStorageProvider, query.getStorageProvider())
                    .like(query.getBucketName() != null, StorageConfig::getBucketName, query.getBucketName())
                    .eq(query.getIsDefault() != null, StorageConfig::getIsDefault, query.getIsDefault());
        }

        wrapper.orderByDesc(StorageConfig::getIsDefault)
                .orderByDesc(StorageConfig::getCreateTime);

        Page<StorageConfig> configPage = new Page<>(page.getCurrent(), page.getSize());
        configPage = page(configPage, wrapper);

        Page<StorageConfigVO> voPage = new Page<>(configPage.getCurrent(), configPage.getSize(), configPage.getTotal());
        voPage.setRecords(configPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public StorageConfigVO getConfigById(Long id) {
        StorageConfig config = getById(id);
        if (config == null) {
            return null;
        }
        return convertToVO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfig(StorageConfig config) {
        if (config.getIsDefault() != null && config.getIsDefault().equals(IS_DEFAULT_YES)) {
            // 如果设置为默认，先取消其他默认配置
            updateDefaultConfig(null);
        }
        return save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(StorageConfig config) {
        if (config.getIsDefault() != null && config.getIsDefault().equals(IS_DEFAULT_YES)) {
            // 如果设置为默认，先取消其他默认配置
            updateDefaultConfig(config.getId());
        }
        return updateById(config);
    }

    @Override
    public boolean deleteConfig(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultConfig(Long id) {
        // 取消当前默认配置
        updateDefaultConfig(id);

        // 设置新的默认配置
        StorageConfig config = getById(id);
        if (config != null) {
            config.setIsDefault(IS_DEFAULT_YES);
            return updateById(config);
        }
        return false;
    }

    /**
     * 更新默认配置
     * <p>
     * 将原来的默认配置设置为非默认
     *
     * @param excludeId 排除的配置ID（更新时排除自己）
     */
    private void updateDefaultConfig(Long excludeId) {
        LambdaQueryWrapper<StorageConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StorageConfig::getIsDefault, IS_DEFAULT_YES);
        if (excludeId != null) {
            wrapper.ne(StorageConfig::getId, excludeId);
        }
        StorageConfig defaultConfig = getOne(wrapper);
        if (defaultConfig != null) {
            defaultConfig.setIsDefault(IS_DEFAULT_NO);
            updateById(defaultConfig);
        }
    }

    @Override
    public StorageConfigVO getDefaultConfig() {
        LambdaQueryWrapper<StorageConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StorageConfig::getIsDefault, IS_DEFAULT_YES);
        StorageConfig config = getOne(wrapper);
        if (config == null) {
            return null;
        }
        return convertToVO(config);
    }

    /**
     * 将 Entity 转换为 VO
     *
     * @param config 存储配置实体
     * @return 存储配置 VO
     */
    private StorageConfigVO convertToVO(StorageConfig config) {
        StorageConfigVO vo = new StorageConfigVO();
        BeanUtils.copyProperties(config, vo);
        vo.setDomain(config.getCustomDomain());
        vo.setBucketAcl(config.getBucketPermission());
        return vo;
    }
}