package dev.illichitcat.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    private ApiDocInterceptor apiDocInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/favicon.ico", "/webjars/**", "/swagger-resources/**")
                .order(1);

        registry.addInterceptor(apiDocInterceptor)
                .addPathPatterns("/doc.html", "/swagger-ui/**", "/v3/api-docs/**")
                .order(2);

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/favicon.ico", "/webjars/**", "/swagger-resources/**",
                        "/doc.html", "/swagger-ui/**", "/v3/api-docs/**")
                .order(3);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Swagger和Knife4j静态资源映射
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}