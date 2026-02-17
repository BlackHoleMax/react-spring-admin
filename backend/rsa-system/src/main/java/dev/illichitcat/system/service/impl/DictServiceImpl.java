package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.system.dao.mapper.DictItemMapper;
import dev.illichitcat.system.dao.mapper.DictMapper;
import dev.illichitcat.system.model.dto.DictExcelDTO;
import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.model.entity.DictItem;
import dev.illichitcat.system.service.DictCacheService;
import dev.illichitcat.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字典服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictItemMapper dictItemMapper;

    @Autowired
    private DictCacheService dictCacheService;

    @Override
    public IPage<Dict> selectDictList(Page<Dict> page, Dict dict) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        if (dict != null) {
            if (dict.getDictName() != null && !dict.getDictName().isEmpty()) {
                queryWrapper.like(Dict::getDictName, dict.getDictName());
            }
            if (dict.getDictCode() != null && !dict.getDictCode().isEmpty()) {
                queryWrapper.like(Dict::getDictCode, dict.getDictCode());
            }
            if (dict.getStatus() != null) {
                queryWrapper.eq(Dict::getStatus, dict.getStatus());
            }
        }
        queryWrapper.orderByDesc(Dict::getCreateTime);
        return dictMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Dict> selectAllEnabled() {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getStatus, 1);
        queryWrapper.orderByAsc(Dict::getSort);
        return dictMapper.selectList(queryWrapper);
    }

    @Override
    public Dict selectDictById(Long id) {
        // 先从缓存获取
        Dict cachedDict = dictCacheService.getDictFromCache(id);
        if (cachedDict != null) {
            log.debug("从缓存获取字典, dictId={}", id);
            return cachedDict;
        }

        // 缓存中没有，从数据库查询
        Dict dict = dictMapper.selectById(id);
        if (dict != null) {
            // 缓存查询结果
            dictCacheService.cacheDict(dict);
            log.debug("从数据库查询字典并缓存, dictId={}", id);
        }

        return dict;
    }

    @Override
    public Dict selectDictByCode(String dictCode) {
        // 先从缓存获取
        Dict cachedDict = dictCacheService.getDictFromCache(dictCode);
        if (cachedDict != null) {
            log.debug("从缓存获取字典, dictCode={}", dictCode);
            return cachedDict;
        }

        // 缓存中没有，从数据库查询
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictCode, dictCode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict != null) {
            // 缓存查询结果
            dictCacheService.cacheDict(dict);
            log.debug("从数据库查询字典并缓存, dictCode={}", dictCode);
        }

        return dict;
    }

    @Override
    public boolean insertDict(Dict dict) {
        log.info("新增字典: {}", dict.getDictName());
        boolean result = dictMapper.insert(dict) > 0;

        if (result) {
            // 清除缓存（新增后清除，下次访问时重新加载）
            dictCacheService.evictDictCache(dict.getDictCode());
            log.info("新增字典成功, dictId={}, dictCode={}", dict.getId(), dict.getDictCode());
        }

        return result;
    }

    @Override
    public boolean updateDict(Dict dict) {
        log.info("更新字典: id={}", dict.getId());
        boolean result = dictMapper.updateById(dict) > 0;

        if (result) {
            // 清除缓存
            dictCacheService.evictDictCache(dict.getId());
            dictCacheService.evictDictCache(dict.getDictCode());

            log.info("更新字典成功并清除缓存, dictId={}, dictCode={}", dict.getId(), dict.getDictCode());
        }

        return result;
    }

    @Override
    public boolean deleteDictById(Long id) {
        log.info("删除字典: id={}", id);

        // 检查字典是否有关联的字典项
        List<DictItem> dictItems = dictItemMapper.selectList(new QueryWrapper<DictItem>().eq("dict_id", id));
        if (!dictItems.isEmpty()) {
            Dict dict = this.getById(id);
            String dictName = dict != null ? dict.getDictName() : String.valueOf(id);
            log.warn("删除字典失败，字典有字典项, dictId={}, dictName={}", id, dictName);
            throw new BizException("字典 " + dictName + " 有字典项，无法删除");
        }

        // 获取字典信息用于缓存清除
        Dict dict = this.getById(id);
        boolean result = dictMapper.deleteById(id) > 0;

        if (result && dict != null) {
            // 清除缓存
            dictCacheService.evictDictCache(dict.getId());
            dictCacheService.evictDictCache(dict.getDictCode());

            log.info("删除字典成功并清除缓存, dictId={}, dictCode={}", dict.getId(), dict.getDictCode());
        }

        return result;
    }

    @Override
    public List<DictExcelDTO> exportDicts(List<Long> dictIds) {
        log.info("导出字典数据开始, dictIds={}", dictIds);

        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        if (dictIds != null && !dictIds.isEmpty()) {
            queryWrapper.in(Dict::getId, dictIds);
        }
        queryWrapper.orderByDesc(Dict::getCreateTime);

        List<Dict> dictList = this.list(queryWrapper);
        List<DictExcelDTO> excelDTOList = new ArrayList<>();

        for (Dict dict : dictList) {
            DictExcelDTO dto = new DictExcelDTO();
            BeanUtils.copyProperties(dict, dto);
            excelDTOList.add(dto);
        }

        log.info("导出字典数据结束, count={}", excelDTOList.size());
        return excelDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importDicts(List<DictExcelDTO> dictExcelDTOList) {
        log.info("导入字典数据开始, count={}", dictExcelDTOList.size());

        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();

        for (int i = 0; i < dictExcelDTOList.size(); i++) {
            DictExcelDTO excelDTO = dictExcelDTOList.get(i);
            // Excel行号（从第2行开始，第1行是表头）
            int rowNum = i + 2;

            try {
                // 验证必填字段
                if (excelDTO.getDictName() == null || excelDTO.getDictName().trim().isEmpty()) {
                    failMessages.append(String.format("第%d行：字典名称不能为空；", rowNum));
                    failCount++;
                    continue;
                }

                if (excelDTO.getDictCode() == null || excelDTO.getDictCode().trim().isEmpty()) {
                    failMessages.append(String.format("第%d行：字典编码不能为空；", rowNum));
                    failCount++;
                    continue;
                }

                // 检查字典编码是否已存在
                Dict existDict = selectDictByCode(excelDTO.getDictCode());
                if (existDict != null) {
                    failMessages.append(String.format("第%d行：字典编码%s已存在；", rowNum, excelDTO.getDictCode()));
                    failCount++;
                    continue;
                }

                // 创建字典
                Dict dict = new Dict();
                BeanUtils.copyProperties(excelDTO, dict);

                // 设置默认值
                if (dict.getSort() == null) {
                    dict.setSort(0);
                }
                if (dict.getStatus() == null) {
                    dict.setStatus(1);
                }

                // 保存字典
                if (this.save(dict)) {
                    successCount++;
                    log.info("成功导入字典, dictName={}, id={}", dict.getDictName(), dict.getId());
                } else {
                    failMessages.append(String.format("第%d行：保存字典失败；", rowNum));
                    failCount++;
                }

            } catch (Exception e) {
                log.error("导入字典失败, row={}, dictName={}, error={}", rowNum, excelDTO.getDictName(), e.getMessage());
                failMessages.append(String.format("第%d行：%s；", rowNum, e.getMessage()));
                failCount++;
            }
        }

        log.info("导入字典数据结束, success={}, fail={}", successCount, failCount);

        String result = String.format("导入完成：成功%d条，失败%d条", successCount, failCount);
        if (failCount > 0) {
            result += "。失败详情：" + failMessages;
        }

        return result;
    }

    @Override
    public boolean deleteDictsByIds(Long[] dictIds) {
        log.info("批量删除字典开始, dictIds={}, count={}", Arrays.toString(dictIds), dictIds.length);

        try {
            // 检查字典是否有关联数据
            for (Long dictId : dictIds) {
                // 检查字典是否有关联的字典项
                List<DictItem> dictItems = dictItemMapper.selectList(new QueryWrapper<DictItem>().eq("dict_id", dictId));
                if (!dictItems.isEmpty()) {
                    Dict dict = this.getById(dictId);
                    String dictName = dict != null ? dict.getDictName() : String.valueOf(dictId);
                    log.warn("批量删除字典失败，字典有字典项, dictId={}, dictName={}", dictId, dictName);
                    throw new BizException("字典 " + dictName + " 有字典项，无法删除");
                }
            }

            // 批量删除字典
            int result = dictMapper.deleteBatchIds(Arrays.asList(dictIds));

            log.info("批量删除字典结束, count={}, result={}", dictIds.length, result);
            return result > 0;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除字典失败", e);
            return false;
        }
    }
}
