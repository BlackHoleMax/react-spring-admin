package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "系统监控", description = "系统监控相关接口")
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired(required = false)
    private HealthEndpoint healthEndpoint;

    @Autowired(required = false)
    private MetricsEndpoint metricsEndpoint;

    @Operation(summary = "获取系统健康状态")
    @GetMapping("/health")
    public Result<Object> getHealth() {
        if (healthEndpoint != null) {
            return Result.ok(healthEndpoint.health());
        }
        return Result.fail("健康检查端点未启用");
    }

    @Operation(summary = "获取系统指标")
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>(16);

        if (metricsEndpoint != null) {
            metrics.put("jvm.memory.used", getMetricValue("jvm.memory.used"));
            metrics.put("jvm.memory.max", getMetricValue("jvm.memory.max"));
            metrics.put("jvm.threads.live", getMetricValue("jvm.threads.live"));
            metrics.put("jvm.threads.peak", getMetricValue("jvm.threads.peak"));
            metrics.put("process.cpu.usage", getMetricValue("process.cpu.usage"));
            metrics.put("system.cpu.usage", getMetricValue("system.cpu.usage"));
            metrics.put("http.server.requests.count", getMetricValue("http.server.requests"));
        }

        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        metrics.put("heap.used", memoryMxBean.getHeapMemoryUsage().getUsed());
        metrics.put("heap.max", memoryMxBean.getHeapMemoryUsage().getMax());
        metrics.put("heap.committed", memoryMxBean.getHeapMemoryUsage().getCommitted());
        metrics.put("non-heap.used", memoryMxBean.getNonHeapMemoryUsage().getUsed());

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        metrics.put("uptime", runtimeMxBean.getUptime());
        metrics.put("start.time", runtimeMxBean.getStartTime());

        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        metrics.put("available.processors", osMxBean.getAvailableProcessors());
        metrics.put("system.load.average", osMxBean.getSystemLoadAverage());

        return Result.ok(metrics);
    }

    @Operation(summary = "获取指定指标详情")
    @GetMapping("/metrics/{name}")
    public Result<Object> getMetric(@PathVariable String name) {
        if (metricsEndpoint != null) {
            return Result.ok(metricsEndpoint.metric(name, null));
        }
        return Result.fail("指标端点未启用");
    }

    @Operation(summary = "获取JVM信息")
    @GetMapping("/jvm")
    public Result<Map<String, Object>> getJvmInfo() {
        Map<String, Object> jvmInfo = new HashMap<>(8);

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        jvmInfo.put("name", runtimeMxBean.getVmName());
        jvmInfo.put("vendor", runtimeMxBean.getVmVendor());
        jvmInfo.put("version", runtimeMxBean.getVmVersion());
        jvmInfo.put("uptime", runtimeMxBean.getUptime());
        jvmInfo.put("startTime", runtimeMxBean.getStartTime());

        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        Map<String, Object> heapMemory = new HashMap<>(4);
        heapMemory.put("init", memoryMxBean.getHeapMemoryUsage().getInit());
        heapMemory.put("used", memoryMxBean.getHeapMemoryUsage().getUsed());
        heapMemory.put("committed", memoryMxBean.getHeapMemoryUsage().getCommitted());
        heapMemory.put("max", memoryMxBean.getHeapMemoryUsage().getMax());
        jvmInfo.put("heapMemory", heapMemory);

        Map<String, Object> nonHeapMemory = new HashMap<>(4);
        nonHeapMemory.put("init", memoryMxBean.getNonHeapMemoryUsage().getInit());
        nonHeapMemory.put("used", memoryMxBean.getNonHeapMemoryUsage().getUsed());
        nonHeapMemory.put("committed", memoryMxBean.getNonHeapMemoryUsage().getCommitted());
        nonHeapMemory.put("max", memoryMxBean.getNonHeapMemoryUsage().getMax());
        jvmInfo.put("nonHeapMemory", nonHeapMemory);

        return Result.ok(jvmInfo);
    }

    @Operation(summary = "获取系统信息")
    @GetMapping("/system")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>(8);

        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        systemInfo.put("name", osMxBean.getName());
        systemInfo.put("arch", osMxBean.getArch());
        systemInfo.put("version", osMxBean.getVersion());
        systemInfo.put("availableProcessors", osMxBean.getAvailableProcessors());
        systemInfo.put("systemLoadAverage", osMxBean.getSystemLoadAverage());

        Runtime runtime = Runtime.getRuntime();
        systemInfo.put("totalMemory", runtime.totalMemory());
        systemInfo.put("freeMemory", runtime.freeMemory());
        systemInfo.put("maxMemory", runtime.maxMemory());

        return Result.ok(systemInfo);
    }

    private Object getMetricValue(String metricName) {
        if (metricsEndpoint == null) {
            return null;
        }
        try {
            var metric = metricsEndpoint.metric(metricName, null);
            if (metric != null && metric.getMeasurements() != null && !metric.getMeasurements().isEmpty()) {
                return metric.getMeasurements().getFirst().getValue();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
