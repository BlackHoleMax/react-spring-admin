package dev.illichitcat.system.config;

import dev.illichitcat.common.common.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
public class MinioConfig implements ApplicationRunner {

    private final MinioProperties minioProperties;

    @Bean
    @ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            MinioClient minioClient = minioClient();
            String bucketName = minioProperties.getBucketName();

            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("MinIO bucket created: {}", bucketName);
            } else {
                log.info("MinIO bucket already exists: {}", bucketName);
            }

            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"AWS": ["*"]},
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build());

            log.info("MinIO bucket policy set to public read for: {}", bucketName);
        } catch (ErrorResponseException e) {
            log.error("MinIO 连接失败！请检查 MinIO 配置是否正确");
            log.error("错误信息: {}", e.getMessage());
            log.error("请确保以下配置正确:");
            log.error("1. MinIO 服务已启动 (默认地址: {})", minioProperties.getEndpoint());
            log.error("2. Access Key 正确 (当前配置: {})", minioProperties.getAccessKey());
            log.error("3. Secret Key 正确");
            log.error("4. 网络连接正常");
            log.error("如需禁用 MinIO 功能，请在配置文件中设置: minio.enabled=false");
            // 不抛出异常，允许应用继续启动
        } catch (Exception e) {
            log.error("MinIO 初始化时发生未知错误: {}", e.getMessage());
            log.error("请检查 MinIO 服务状态和网络连接");
            // 不抛出异常，允许应用继续启动
        }
    }
}
