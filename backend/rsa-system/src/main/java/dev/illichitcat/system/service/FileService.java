package dev.illichitcat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.query.FileQuery;

/**
 * 文件管理服务接口
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
public interface FileService extends IService<File> {

    /**
     * 分页查询文件列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 文件列表
     */
    IPage<File> selectFilePage(IPage<File> page, FileQuery query);

    /**
     * 根据ID删除文件（逻辑删除）
     *
     * @param id 文件ID
     * @return 是否成功
     */
    boolean deleteFileById(Long id);

    /**
     * 批量删除文件（逻辑删除）
     *
     * @param ids 文件ID列表
     * @return 是否成功
     */
    boolean deleteFileByIds(Long[] ids);
}