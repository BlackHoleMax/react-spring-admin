package dev.illichitcat.common.utils;

import dev.illichitcat.common.common.properties.MinioProperties;
import dev.illichitcat.common.exception.BizException;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Component
public class MinioUtils {

    private static final String FILE_EXT_DOT = ".";

    @Autowired(required = false)
    private MinioClient minioClient;

    @Autowired(required = false)
    private MinioProperties minioProperties;

    public String uploadFile(MultipartFile file) {
        if (minioClient == null || minioProperties == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return getFileUrl(fileName);
        } catch (Exception e) {
            log.error("MinIO file upload failed", e);
            throw new BizException("文件上传失败");
        }
    }

    public String uploadFile(String fileName, InputStream inputStream, String contentType, long size) {
        if (minioClient == null || minioProperties == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            return getFileUrl(fileName);
        } catch (Exception e) {
            log.error("MinIO file upload failed", e);
            throw new BizException("文件上传失败");
        }
    }

    /**
     * 使用自定义配置上传文件
     *
     * @param file       文件
     * @param endpoint   访问站点
     * @param accessKey  访问密钥
     * @param secretKey  密钥
     * @param bucketName 桶名称
     * @param isHttps    是否HTTPS
     * @return 文件URL
     */
    public String uploadFile(MultipartFile file, String endpoint, String accessKey, String secretKey, String bucketName, Boolean isHttps) {
        try {
            MinioClient client = createMinioClient(endpoint, accessKey, secretKey, isHttps);
            String fileName = generateUniqueFileName(file.getOriginalFilename());

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return getFileUrl(endpoint, bucketName, fileName);
        } catch (Exception e) {
            log.error("MinIO file upload failed", e);
            throw new BizException("文件上传失败");
        }
    }

    /**
     * 使用自定义配置上传文件（流方式）
     *
     * @param fileName    文件名
     * @param inputStream 输入流
     * @param contentType 内容类型
     * @param size        文件大小
     * @param endpoint    访问站点
     * @param accessKey   访问密钥
     * @param secretKey   密钥
     * @param bucketName  桶名称
     * @param isHttps     是否HTTPS
     * @return 文件URL
     */
    public String uploadFile(String fileName, InputStream inputStream, String contentType, long size, String endpoint, String accessKey, String secretKey, String bucketName, Boolean isHttps) {
        try {
            MinioClient client = createMinioClient(endpoint, accessKey, secretKey, isHttps);
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            return getFileUrl(endpoint, bucketName, fileName);
        } catch (Exception e) {
            log.error("MinIO file upload failed", e);
            throw new BizException("文件上传失败");
        }
    }

    /**
     * 创建 MinIO 客户端
     *
     * @param endpoint  访问站点
     * @param accessKey 访问密钥
     * @param secretKey 密钥
     * @param isHttps   是否HTTPS
     * @return MinIO 客户端
     */
    private MinioClient createMinioClient(String endpoint, String accessKey, String secretKey, Boolean isHttps) {
        try {
            // 移除 http:// 或 https:// 前缀
            String cleanEndpoint = endpoint.replaceFirst("^https?://", "");
            String protocol = isHttps != null && isHttps ? "https" : "http";
            return MinioClient.builder()
                    .endpoint(protocol + "://" + cleanEndpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            log.error("创建 MinIO 客户端失败: endpoint={}", endpoint, e);
            throw new BizException("创建 MinIO 客户端失败");
        }
    }

    /**
     * 获取文件URL（自定义配置）
     *
     * @param endpoint   访问站点
     * @param bucketName 桶名称
     * @param fileName   文件名
     * @return 文件URL
     */
    private String getFileUrl(String endpoint, String bucketName, String fileName) {
        String protocol = endpoint.startsWith("https") ? "https" : "http";
        String cleanEndpoint = endpoint.replaceFirst("^https?://", "");
        return protocol + "://" + cleanEndpoint + "/" + bucketName + "/" + fileName;
    }

    public InputStream downloadFile(String fileName) {
        if (minioClient == null || minioProperties == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO file download failed", e);
            throw new BizException("文件下载失败");
        }
    }

    public void deleteFile(String fileName) {
        if (minioClient == null || minioProperties == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO file delete failed", e);
            throw new BizException("文件删除失败");
        }
    }

    /**
     * 删除文件（支持自定义 bucket）
     *
     * @param bucketName 桶名称
     * @param fileName   文件名
     */
    public void deleteFile(String bucketName, String fileName) {
        if (minioClient == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("MinIO 文件删除成功: bucket={}, fileName={}", bucketName, fileName);
        } catch (Exception e) {
            log.error("MinIO file delete failed: bucket={}, fileName={}", bucketName, fileName, e);
            throw new BizException("文件删除失败");
        }
    }

    public String getFileUrl(String fileName) {
        if (minioProperties == null) {
            throw new BizException("MinIO 未配置");
        }
        return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + fileName;
    }

    public String getPresignedUrl(String fileName, int expiry, TimeUnit unit) {
        if (minioClient == null || minioProperties == null) {
            throw new BizException("MinIO 未配置或连接失败");
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .expiry((int) unit.toSeconds(expiry), TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO get presigned URL failed", e);
            throw new BizException("获取文件URL失败");
        }
    }

    public boolean fileExists(String fileName) {
        if (minioClient == null || minioProperties == null) {
            return false;
        }
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = extractFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 提取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    private String extractFileExtension(String filename) {
        if (filename == null || !filename.contains(FILE_EXT_DOT)) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
