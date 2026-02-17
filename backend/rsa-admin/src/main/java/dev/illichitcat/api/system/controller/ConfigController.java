package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "系统配置管理", description = "系统配置相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取验证码配置（无需登录）
     *
     * @return CaptchaSettings
     */
    @Operation(
            summary = "获取验证码配置",
            description = "获取验证码开关配置，无需登录"
    )
    @GetMapping("/captcha")
    public Result<CaptchaSettings> getCaptchaConfig() {
        CaptchaSettings settings = new CaptchaSettings();
        boolean enabled = configService.getBooleanValue("captcha.login.enabled", true);
        settings.setLoginEnabled(enabled);
        return Result.ok(settings);
    }

    /**
     * 获取系统配置
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Operation(
            summary = "获取系统配置",
            description = "根据配置键获取配置值"
    )
    @RequirePermission("system:config")
    @GetMapping("/{configKey}")
    public Result<String> getConfig(@PathVariable String configKey) {
        String value = configService.getConfigValue(configKey);
        return Result.ok(value);
    }

    /**
     * 更新系统配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return Result
     */
    @Operation(
            summary = "更新系统配置",
            description = "根据配置键更新配置值"
    )
    @RequirePermission("system:config")
    @PutMapping("/{configKey}")
    @OperationLog(title = "系统配置", businessType = OperationLog.BusinessType.UPDATE)
    public Result<Void> updateConfig(
            @PathVariable String configKey,
            @RequestBody String configValue) {
        boolean success = configService.updateConfig(configKey, configValue);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail("更新配置失败，配置键不存在");
        }
    }

    /**
     * 更新或新增系统配置
     *
     * @param configRequest 配置请求
     * @return Result
     */
    @Operation(
            summary = "更新或新增系统配置",
            description = "根据配置键更新或新增配置值"
    )
    @RequirePermission("system:config")
    @PostMapping
    @OperationLog(title = "系统配置", businessType = OperationLog.BusinessType.INSERT)
    public Result<Void> saveOrUpdateConfig(@RequestBody ConfigRequest configRequest) {
        boolean success = configService.saveOrUpdateConfig(
                configRequest.getConfigKey(),
                configRequest.getConfigValue(),
                configRequest.getConfigName(),
                configRequest.getRemark()
        );
        if (success) {
            return Result.ok();
        } else {
            return Result.fail("保存或更新配置失败");
        }
    }

    /**
     * 验证码设置DTO
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    public static class CaptchaSettings {
        private boolean loginEnabled;
    }

    /**
     * 配置请求DTO
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    public static class ConfigRequest {
        @Parameter(description = "配置键")
        private String configKey;

        @Parameter(description = "配置值")
        private String configValue;

        @Parameter(description = "配置名称")
        private String configName;

        @Parameter(description = "备注")
        private String remark;
    }
}