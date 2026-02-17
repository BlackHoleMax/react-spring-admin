package dev.illichitcat.common.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private List<String> ignoreUrls = List.of();
}