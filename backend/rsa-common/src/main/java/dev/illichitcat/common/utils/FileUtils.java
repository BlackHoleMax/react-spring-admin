package dev.illichitcat.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文件工具类
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
public class FileUtils {

    /**
     * 文件分类：图片
     */
    public static final String FILE_CATEGORY_IMAGE = "image";
    /**
     * 文件分类：视频
     */
    public static final String FILE_CATEGORY_VIDEO = "video";
    /**
     * 文件分类：音频
     */
    public static final String FILE_CATEGORY_AUDIO = "audio";
    /**
     * 文件分类：文档
     */
    public static final String FILE_CATEGORY_DOCUMENT = "document";
    /**
     * 文件分类：其他
     */
    public static final String FILE_CATEGORY_OTHER = "other";
    /**
     * MIME 类型前缀：图片
     */
    private static final String MIME_TYPE_PREFIX_IMAGE = "image/";
    /**
     * MIME 类型前缀：视频
     */
    private static final String MIME_TYPE_PREFIX_VIDEO = "video/";
    /**
     * MIME 类型前缀：音频
     */
    private static final String MIME_TYPE_PREFIX_AUDIO = "audio/";
    /**
     * MIME 类型前缀：文本
     */
    private static final String MIME_TYPE_PREFIX_TEXT = "text/";
    /**
     * MIME 类型标识：PDF
     */
    private static final String MIME_TYPE_PDF = "pdf";

    private FileUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }

    /**
     * 根据MIME类型获取文件分类
     *
     * @param mimeType MIME类型
     * @return 文件分类（image图片、video视频、audio音频、document文档、other其他）
     */
    public static String getFileCategory(String mimeType) {
        if (mimeType == null) {
            return FILE_CATEGORY_OTHER;
        }
        if (mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE)) {
            return FILE_CATEGORY_IMAGE;
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)) {
            return FILE_CATEGORY_VIDEO;
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)) {
            return FILE_CATEGORY_AUDIO;
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_TEXT) || mimeType.contains(MIME_TYPE_PDF) ||
                mimeType.contains("document") || mimeType.contains("sheet") ||
                mimeType.contains("presentation")) {
            return FILE_CATEGORY_DOCUMENT;
        }
        return FILE_CATEGORY_OTHER;
    }

    /**
     * 构建文件信息对象
     *
     * @param file             上传的文件
     * @param originalFilename 原始文件名
     * @param url              文件URL
     * @param storageProvider  存储服务商
     * @param bucketName       存储桶名称
     * @param userId           用户ID
     * @param username         用户名
     * @return 文件信息对象
     */
    public static <T> T buildFileInfo(MultipartFile file, String originalFilename, String url,
                                      String storageProvider, String bucketName,
                                      Long userId, String username, Class<T> fileInfoClass) {
        try {
            String fileSuffix = getFileExtension(originalFilename);
            String fileCategory = getFileCategory(file.getContentType());

            T fileInfo = fileInfoClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Field fileNameField = fileInfoClass.getDeclaredField("fileName");
            fileNameField.setAccessible(true);
            fileNameField.set(fileInfo, originalFilename);

            java.lang.reflect.Field originalNameField = fileInfoClass.getDeclaredField("originalName");
            originalNameField.setAccessible(true);
            originalNameField.set(fileInfo, originalFilename);

            java.lang.reflect.Field fileSuffixField = fileInfoClass.getDeclaredField("fileSuffix");
            fileSuffixField.setAccessible(true);
            fileSuffixField.set(fileInfo, fileSuffix);

            java.lang.reflect.Field filePathField = fileInfoClass.getDeclaredField("filePath");
            filePathField.setAccessible(true);
            filePathField.set(fileInfo, url);

            java.lang.reflect.Field fileUrlField = fileInfoClass.getDeclaredField("fileUrl");
            fileUrlField.setAccessible(true);
            fileUrlField.set(fileInfo, url);

            java.lang.reflect.Field fileSizeField = fileInfoClass.getDeclaredField("fileSize");
            fileSizeField.setAccessible(true);
            fileSizeField.set(fileInfo, file.getSize());

            java.lang.reflect.Field fileTypeField = fileInfoClass.getDeclaredField("fileType");
            fileTypeField.setAccessible(true);
            fileTypeField.set(fileInfo, file.getContentType());

            java.lang.reflect.Field fileCategoryField = fileInfoClass.getDeclaredField("fileCategory");
            fileCategoryField.setAccessible(true);
            fileCategoryField.set(fileInfo, fileCategory);

            java.lang.reflect.Field storageProviderField = fileInfoClass.getDeclaredField("storageProvider");
            storageProviderField.setAccessible(true);
            storageProviderField.set(fileInfo, storageProvider);

            java.lang.reflect.Field bucketNameField = fileInfoClass.getDeclaredField("bucketName");
            bucketNameField.setAccessible(true);
            bucketNameField.set(fileInfo, bucketName);

            java.lang.reflect.Field uploadUserIdField = fileInfoClass.getDeclaredField("uploadUserId");
            uploadUserIdField.setAccessible(true);
            uploadUserIdField.set(fileInfo, userId);

            java.lang.reflect.Field uploadUserNameField = fileInfoClass.getDeclaredField("uploadUserName");
            uploadUserNameField.setAccessible(true);
            uploadUserNameField.set(fileInfo, username);

            java.lang.reflect.Field createTimeField = fileInfoClass.getDeclaredField("createTime");
            createTimeField.setAccessible(true);
            createTimeField.set(fileInfo, LocalDateTime.now());

            java.lang.reflect.Field updateTimeField = fileInfoClass.getDeclaredField("updateTime");
            updateTimeField.setAccessible(true);
            updateTimeField.set(fileInfo, LocalDateTime.now());

            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException("构建文件信息失败", e);
        }
    }
}