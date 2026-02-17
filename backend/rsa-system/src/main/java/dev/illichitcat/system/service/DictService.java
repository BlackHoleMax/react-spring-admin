package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.dto.DictExcelDTO;
import dev.illichitcat.system.model.entity.Dict;

import java.util.List;

/**
 * 字典服务接口
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
public interface DictService extends IService<Dict> {

    /**
     * 分页查询字典列表
     *
     * @param page 分页对象
     * @param dict 查询条件
     * @return 字典分页列表
     */
    IPage<Dict> selectDictList(Page<Dict> page, Dict dict);

    /**
     * 获取所有启用的字典列表
     *
     * @return 字典列表
     */
    List<Dict> selectAllEnabled();

    /**
     * 根据ID查询字典
     *
     * @param id 字典ID
     * @return 字典信息
     */
    Dict selectDictById(Long id);

    /**
     * 根据字典编码查询字典
     *
     * @param dictCode 字典编码
     * @return 字典信息
     */
    Dict selectDictByCode(String dictCode);

    /**
     * 新增字典
     *
     * @param dict 字典信息
     * @return 是否成功
     */
    boolean insertDict(Dict dict);

    /**
     * 更新字典
     *
     * @param dict 字典信息
     * @return 是否成功
     */
    boolean updateDict(Dict dict);

    /**
     * 删除字典
     *
     * @param id 字典ID
     * @return 是否成功
     */
    boolean deleteDictById(Long id);

    /**
     * 导出字典数据到Excel
     *
     * @param dictIds 字典ID列表，为空则导出所有
     * @return 字典Excel数据列表
     */
    List<DictExcelDTO> exportDicts(List<Long> dictIds);

    /**
     * 从Excel导入字典数据
     *
     * @param dictExcelDTOList 字典Excel数据列表
     * @return 导入结果信息
     */
    String importDicts(List<DictExcelDTO> dictExcelDTOList);

    /**
     * 批量删除字典
     *
     * @param dictIds 字典ID数组
     * @return 删除结果
     */
    boolean deleteDictsByIds(Long[] dictIds);
}