package dev.illichitcat;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@SpringBootApplication
@EnableScheduling
@MapperScan({
        "dev.illichitcat.system.dao.mapper",
        "dev.illichitcat.quartz.dao.mapper",
        "dev.illichitcat.generator.dao.mapper"
})
public class BackendApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("""
                  _____                 _      _____            _                           _           _
                 |  __ \\               | |    / ____|          (_)                 /\\      | |         (_)
                 | |__) |___  __ _  ___| |_  | (___  _ __  _ __ _ _ __   __ _     /  \\   __| |_ __ ___  _ _ __
                 |  _  // _ \\/ _` |/ __| __|  \\___ \\| '_ \\| '__| | '_ \\ / _` |   / /\\ \\ / _` | '_ ` _ \\| | '_ \\
                 | | \\ \\  __/ (_| | (__| |_   ____) | |_) | |  | | | | | (_| |  / ____ \\ (_| | | | | | | | | | |
                 |_|  \\_\\___|\\__,_|\\___|\\__| |_____/| .__/|_|  |_|_| |_|\\__, | /_/    \\_\\__,_|_| |_| |_|_|_| |_|
                                                    | |                  __/ |
                                                    |_|                 |___/                                   \
                """);
    }

}