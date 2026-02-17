package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.DictItem;

import java.util.List;

/**
 * 字典项服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface DictItemService extends IService<DictItem> {

    /**
     * 根据字典ID查询字典项列表
     *
     * @param dictId 字典ID
     * @return 字典项列表
     */
    List<DictItem> selectItemsByDictId(Long dictId);

    /**
     * 根据字典编码查询字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<DictItem> selectItemsByDictCode(String dictCode);

    /**
     * 根据ID查询字典项
     *
     * @param id 字典项ID
     * @return 字典项信息
     */
    DictItem selectItemById(Long id);

    /**
     * 新增字典项
     *
     * @param item 字典项信息
     * @return 是否成功
     */
    boolean insertItem(DictItem item);

    /**
     * 更新字典项
     *
     * @param item 字典项信息
     * @return 是否成功
     */
    boolean updateItem(DictItem item);

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return 是否成功
     */
    boolean deleteItemById(Long id);

    /**
     * 根据字典ID删除字典项
     *
     * @param dictId 字典ID
     * @return 是否成功
     */
    boolean deleteItemsByDictId(Long dictId);
}