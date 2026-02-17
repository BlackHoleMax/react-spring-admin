package dev.illichitcat.api;

import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "通用接口", description = "提供系统状态查询等通用功能")
@RestController
@RequestMapping("/api")
public class CommonController {

    /**
     * 获取服务器运行状态
     *
     * @return SystemStatus
     */
    @Operation(
            summary = "获取系统状态",
            description = "获取服务器操作系统、JVM等相关运行状态信息"
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功获取系统状态信息",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SystemStatus.class)
            )
    )
    @RequirePermission("system:status")
    @GetMapping("/status")
    public Result<SystemStatus> status() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        SystemStatus status = new SystemStatus();
        status.setOsName(osBean.getName());
        status.setOsVersion(osBean.getVersion());
        status.setAvailableProcessors(osBean.getAvailableProcessors());
        status.setSystemLoadAverage(osBean.getSystemLoadAverage());
        status.setUptime(runtimeBean.getUptime());
        status.setVmVendor(runtimeBean.getVmVendor());
        status.setVmVersion(runtimeBean.getVmVersion());

        return Result.ok(status);
    }

    /**
     * 返回空白的 favicon.ico notFound
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Favicon图标",
            description = "处理浏览器对favicon.ico的请求，返回404状态"
    )
    @ApiResponse(
            responseCode = "404",
            description = "总是返回404状态，表示没有favicon图标"
    )
    @GetMapping("favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.notFound().build();
    }

    /**
     * 内部类用于表示系统状态
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Data
    @Schema(description = "系统状态信息")
    public static class SystemStatus {
        @Schema(description = "操作系统名称")
        private String osName;

        @Schema(description = "操作系统版本")
        private String osVersion;

        @Schema(description = "可用处理器数量")
        private int availableProcessors;

        @Schema(description = "系统负载平均值")
        private double systemLoadAverage;

        @Schema(description = "JVM运行时间(毫秒)")
        private long uptime;

        @Schema(description = "JVM供应商")
        private String vmVendor;

        @Schema(description = "JVM版本")
        private String vmVersion;
    }
}