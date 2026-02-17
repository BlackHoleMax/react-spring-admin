package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.utils.MinioUtils;
import dev.illichitcat.system.dao.mapper.FileMapper;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.query.FileQuery;
import dev.illichitcat.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 文件管理服务实现类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    /**
     * 存储服务商：MinIO
     */
    private static final String STORAGE_PROVIDER_MINIO = "minio";

    @Autowired(required = false)
    private MinioUtils minioUtils;

    @Override
    public IPage<File> selectFilePage(IPage<File> page, FileQuery query) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();

        // 文件名模糊查询
        if (StringUtils.isNotBlank(query.getFileName())) {
            wrapper.like(File::getFileName, query.getFileName());
        }

        // 原始文件名模糊查询
        if (StringUtils.isNotBlank(query.getOriginalName())) {
            wrapper.like(File::getOriginalName, query.getOriginalName());
        }

        // 文件后缀查询
        if (StringUtils.isNotBlank(query.getFileSuffix())) {
            wrapper.eq(File::getFileSuffix, query.getFileSuffix());
        }

        // 创建时间范围查询
        if (query.getCreateTimeStart() != null) {
            wrapper.ge(File::getCreateTime, query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.le(File::getCreateTime, query.getCreateTimeEnd());
        }

        // 存储服务商查询
        if (StringUtils.isNotBlank(query.getStorageProvider())) {
            wrapper.eq(File::getStorageProvider, query.getStorageProvider());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(File::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileById(Long id) {
        // 先查询文件信息
        File file = this.getById(id);
        if (file == null) {
            log.warn("文件不存在，ID: {}", id);
            return false;
        }

        // 从 MinIO 删除文件
        deleteFileFromMinio(file);

        // 删除数据库记录
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileByIds(Long[] ids) {
        // 先查询所有文件信息
        List<File> files = this.listByIds(Arrays.asList(ids));
        if (files.isEmpty()) {
            log.warn("文件不存在，IDs: {}", Arrays.toString(ids));
            return false;
        }

        // 从 MinIO 批量删除文件
        if (minioUtils != null) {
            for (File file : files) {
                if (STORAGE_PROVIDER_MINIO.equals(file.getStorageProvider())) {
                    deleteFileFromMinio(file);
                }
            }
        }

        // 删除数据库记录
        return this.removeBatchByIds(Arrays.asList(ids));
    }

    /**
     * 从 MinIO 删除文件
     *
     * @param file 文件信息
     */
    private void deleteFileFromMinio(File file) {
        if (minioUtils == null || !STORAGE_PROVIDER_MINIO.equals(file.getStorageProvider())) {
            return;
        }

        try {
            // 从 fileUrl 中提取实际的文件名
            String actualFileName = extractFileNameFromUrl(file.getFileUrl(), file.getBucketName());

            if (actualFileName != null && !actualFileName.isEmpty()) {
                // 使用 MinioUtils 删除文件，传入实际的 bucketName
                minioUtils.deleteFile(file.getBucketName(), actualFileName);
                log.info("从 MinIO 删除文件成功: bucket={}, fileName={}", file.getBucketName(), actualFileName);
            } else {
                log.warn("无法从 URL 中提取文件名: {}", file.getFileUrl());
            }
        } catch (Exception e) {
            log.error("从 MinIO 删除文件失败: {}", file.getFileUrl(), e);
            // 即使 MinIO 删除失败，也继续删除数据库记录
        }
    }

    /**
     * 从 URL 中提取 MinIO 实际存储的文件名
     *
     * @param fileUrl    文件URL
     * @param bucketName 桶名称
     * @return 文件名
     */
    private String extractFileNameFromUrl(String fileUrl, String bucketName) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        try {
            // fileUrl 格式: endpoint/bucketName/fileName
            // 例如: http://172.16.29.222:9000/avatars/4137cef1725b4cd59190e75653d64fc5.jfif
            String prefix = "/" + bucketName + "/";
            int index = fileUrl.indexOf(prefix);

            if (index != -1) {
                // 提取 bucketName 之后的部分作为文件名
                String fileName = fileUrl.substring(index + prefix.length());
                // 移除 URL 参数（如果有）
                int queryIndex = fileName.indexOf('?');
                if (queryIndex != -1) {
                    fileName = fileName.substring(0, queryIndex);
                }
                log.debug("从 URL 提取文件名成功: URL={}, bucketName={}, fileName={}", fileUrl, bucketName, fileName);
                return fileName;
            }

            log.warn("无法从 URL 中提取文件名，URL: {}, bucketName: {}", fileUrl, bucketName);
            return null;
        } catch (Exception e) {
            log.error("提取文件名失败: {}", fileUrl, e);
            return null;
        }
    }
}