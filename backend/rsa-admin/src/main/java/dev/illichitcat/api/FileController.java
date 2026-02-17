package dev.illichitcat.api;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.FileUtils;
import dev.illichitcat.common.utils.MinioUtils;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.vo.StorageConfigVO;
import dev.illichitcat.system.service.FileService;
import dev.illichitcat.system.service.StorageConfigService;
import dev.illichitcat.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "文件上传")
@RestController
@Slf4j
@RequestMapping("/api/file")
@RequiredArgsConstructor
@ConditionalOnBean(MinioUtils.class)
public class FileController {

    private final MinioUtils minioUtils;
    private final FileService fileService;
    private final UserService userService;
    private final StorageConfigService storageConfigService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 获取默认配置
        StorageConfigVO defaultConfig = storageConfigService.getDefaultConfig();
        if (defaultConfig == null) {
            log.error("未找到默认存储配置");
            return Result.fail("未找到默认存储配置，请先配置存储服务");
        }

        String url;
        String originalFilename = file.getOriginalFilename();
        String bucketName = defaultConfig.getBucketName();

        try {
            // 使用默认配置上传文件
            url = minioUtils.uploadFile(
                    file,
                    defaultConfig.getEndpoint(),
                    defaultConfig.getAccessKey(),
                    defaultConfig.getSecretKey(),
                    bucketName,
                    defaultConfig.getIsHttps() == 1
            );
        } catch (Exception e) {
            log.error("文件上传失败: configKey={}", defaultConfig.getConfigKey(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }

        // 保存文件信息到数据库
        saveFileInfo(file, originalFilename, url, bucketName, defaultConfig, request);

        Map<String, String> result = new HashMap<>(2);
        result.put("url", url);
        result.put("fileName", originalFilename);
        return Result.ok(result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileName}")
    public Result<Void> deleteFile(@PathVariable String fileName) {
        minioUtils.deleteFile(fileName);
        return Result.ok();
    }

    /**
     * 保存文件信息到数据库
     */
    private void saveFileInfo(MultipartFile file, String originalFilename, String url, String bucketName, StorageConfigVO config, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");

            File fileInfo = FileUtils.buildFileInfo(file, originalFilename, url, config.getStorageProvider(),
                    bucketName, userId, username, File.class);
            fileService.save(fileInfo);
        } catch (Exception e) {
            // 保存文件信息失败不影响上传结果，只记录日志
            log.error("保存文件信息到数据库失败", e);
        }
    }
}
