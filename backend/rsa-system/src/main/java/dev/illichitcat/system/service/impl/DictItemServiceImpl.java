package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.system.dao.mapper.DictItemMapper;
import dev.illichitcat.system.dao.mapper.DictMapper;
import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.model.entity.DictItem;
import dev.illichitcat.system.service.DictCacheService;
import dev.illichitcat.system.service.DictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 字典项服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    @Autowired
    private DictItemMapper dictItemMapper;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictCacheService dictCacheService;

    @Override
    public List<DictItem> selectItemsByDictId(Long dictId) {
        // 直接从数据库查询
        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItem::getDictId, dictId)
                .eq(DictItem::getStatus, 1)
                .orderByAsc(DictItem::getSort);
        List<DictItem> items = dictItemMapper.selectList(queryWrapper);

        log.debug("从数据库查询字典项列表, dictId={}, itemCount={}", dictId, items.size());

        return items;
    }

    @Override
    public List<DictItem> selectItemsByDictCode(String dictCode) {
        // 直接从数据库查询
        LambdaQueryWrapper<Dict> dictQuery = new LambdaQueryWrapper<>();
        dictQuery.eq(Dict::getDictCode, dictCode);
        Dict dict = dictMapper.selectOne(dictQuery);
        if (dict == null) {
            return Collections.emptyList();
        }

        List<DictItem> items = selectItemsByDictId(dict.getId());

        log.debug("从数据库查询字典项列表, dictCode={}, itemCount={}", dictCode, items.size());

        return items;
    }

    @Override
    public DictItem selectItemById(Long id) {
        return dictItemMapper.selectById(id);
    }

    @Override
    public boolean insertItem(DictItem item) {
        log.info("新增字典项: dictId={}, text={}", item.getDictId(), item.getItemText());
        boolean result = dictItemMapper.insert(item) > 0;

        if (result) {
            // 清除字典项列表缓存
            dictCacheService.evictDictItemsCache(item.getDictId());

            // 获取字典编码用于清除缓存
            Dict dict = dictMapper.selectById(item.getDictId());
            if (dict != null) {
                dictCacheService.evictDictItemsCache(dict.getDictCode());
            }

            log.info("新增字典项成功并清除缓存, itemId={}, dictId={}", item.getId(), item.getDictId());
        }

        return result;
    }

    @Override
    public boolean updateItem(DictItem item) {
        log.info("更新字典项: id={}", item.getId());
        boolean result = dictItemMapper.updateById(item) > 0;

        if (result) {
            // 清除字典项缓存
            dictCacheService.evictDictItemCache(item.getId());

            // 清除字典项列表缓存
            dictCacheService.evictDictItemsCache(item.getDictId());

            // 获取字典编码用于清除缓存
            Dict dict = dictMapper.selectById(item.getDictId());
            if (dict != null) {
                dictCacheService.evictDictItemsCache(dict.getDictCode());
            }

            log.info("更新字典项成功并清除缓存, itemId={}, dictId={}", item.getId(), item.getDictId());
        }

        return result;
    }

    @Override
    public boolean deleteItemById(Long id) {
        log.info("删除字典项: id={}", id);

        // 获取字典项信息用于缓存清除
        DictItem item = dictItemMapper.selectById(id);
        boolean result = dictItemMapper.deleteById(id) > 0;

        if (result && item != null) {
            // 清除字典项缓存
            dictCacheService.evictDictItemCache(item.getId());

            // 清除字典项列表缓存
            dictCacheService.evictDictItemsCache(item.getDictId());

            // 获取字典编码用于清除缓存
            Dict dict = dictMapper.selectById(item.getDictId());
            if (dict != null) {
                dictCacheService.evictDictItemsCache(dict.getDictCode());
            }

            log.info("删除字典项成功并清除缓存, itemId={}, dictId={}", item.getId(), item.getDictId());
        }

        return result;
    }

    @Override
    public boolean deleteItemsByDictId(Long dictId) {
        log.info("删除字典项: dictId={}", dictId);

        // 获取字典编码用于缓存清除
        Dict dict = dictMapper.selectById(dictId);

        LambdaQueryWrapper<DictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItem::getDictId, dictId);
        boolean result = dictItemMapper.delete(queryWrapper) >= 0;

        if (result) {
            // 清除字典项列表缓存
            dictCacheService.evictDictItemsCache(dictId);

            if (dict != null) {
                dictCacheService.evictDictItemsCache(dict.getDictCode());
            }

            log.info("删除字典项成功并清除缓存, dictId={}", dictId);
        }

        return result;
    }
}
