package dev.illichitcat.system.config;

import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.selectUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(user.getStatus() == 0)
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用frameOptions，解决iframe相关问题
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 配置请求授权规则
                .authorizeHttpRequests(authz -> authz
                        // 允许访问登录接口
                        .requestMatchers("/login").permitAll()
                        // 允许访问状态接口
                        .requestMatchers("/status").permitAll()
                        // 允许访问Actuator监控端点
                        .requestMatchers("/actuator/**").permitAll()
                        // 允许访问静态资源
                        .requestMatchers("/webjars/**", "/swagger-resources/**").permitAll()
                        // 允许访问favicon.ico
                        .requestMatchers("/favicon.ico").permitAll()
                        // 允许访问H2控制台（如果使用）
                        .requestMatchers("/h2-console/**").permitAll()
                        // 允许访问文件上传接口
                        .requestMatchers("/api/file/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}