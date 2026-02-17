package dev.illichitcat.api;

import dev.illichitcat.common.common.properties.MinioProperties;
import dev.illichitcat.common.common.result.Result;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "系统健康检查")
@RestController
@RequestMapping("/api/health")
@Slf4j
public class SystemHealthController {

    @Autowired(required = false)
    private MinioProperties minioProperties;

    @Autowired(required = false)
    private MinioClient minioClient;

    @Operation(summary = "检查系统各组件状态")
    @GetMapping("/status")
    public Result<Map<String, Object>> checkSystemStatus() {
        Map<String, Object> status = new HashMap<>(4);

        // 检查 MinIO 状态
        Map<String, Object> minioStatus = new HashMap<>(8);
        if (minioClient == null || minioProperties == null) {
            minioStatus.put("enabled", false);
            minioStatus.put("status", "not configured");
            minioStatus.put("suggestion", "MinIO 未配置或连接失败");
        } else {
            minioStatus.put("enabled", true);
            minioStatus.put("endpoint", minioProperties.getEndpoint());

            try {
                boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build());
                minioStatus.put("status", "connected");
                minioStatus.put("bucket", minioProperties.getBucketName());
                minioStatus.put("bucketExists", bucketExists);
            } catch (Exception e) {
                minioStatus.put("status", "disconnected");
                minioStatus.put("error", e.getMessage());
                minioStatus.put("suggestion", "请检查 MinIO 服务是否正常运行，配置是否正确");
            }
        }

        status.put("minio", minioStatus);

        return Result.ok(status);
    }
}