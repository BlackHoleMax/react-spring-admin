package dev.illichitcat.system.config;

import dev.illichitcat.common.utils.SecurePasswordGenerator;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化类
 * 在应用启动时初始化默认数据
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final int RANDOMLENGTH = 8;
    private static final String CONFIG_NAME = "dev";

    @Autowired
    private UserService userService;
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    public void run(String... args) throws Exception {
        // 只在开发环境(dev)下初始化数据
        if (activeProfile.contains(CONFIG_NAME)) {
            initializeDefaultAdmin();
        }
    }

    /**
     * 初始化默认管理员账号
     */
    private void initializeDefaultAdmin() {
        // 默认管理员账号信息
        String username = "admin";
        String rawPassword = new SecurePasswordGenerator().generate(RANDOMLENGTH, false, true, false);

        // 检查是否已存在admin用户
        User adminUser = userService.selectUserByUsername(username);

        if (adminUser == null) {
            // 创建默认管理员账号
            adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setNickname("管理员");

            // 加密密码
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(rawPassword);
            adminUser.setPassword(encodedPassword);

            // 设置其他属性
            adminUser.setStatus(1);
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("13800138000");

            // 保存用户
            boolean success = userService.insertUser(adminUser);
            logUserInfo(success, username, rawPassword);
        } else {
            // 更新admin密码
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(rawPassword);
            adminUser.setPassword(encodedPassword);

            boolean success = userService.updateUser(adminUser);
            logUserInfo(success, username, rawPassword);
        }
    }

    private void logUserInfo(boolean success, String username, String rawPassword) {
        if (success) {
            logger.info("默认管理员账号密码已更新！");
            logger.info("用户名: {}", username);
            logger.info("密码: {}", rawPassword);
            logger.info("请妥善保管此账号信息，如有需要请及时修改密码！");
        } else {
            logger.error("默认管理员账号密码更新失败！");
        }
    }
}