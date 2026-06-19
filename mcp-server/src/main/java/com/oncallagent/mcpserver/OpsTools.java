package com.oncallagent.mcpserver;

import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Mock 运维工具,通过 MCP 对外暴露。数据是一条连贯故事线:
 * data-sync-service 同步积压 → 线程池耗尽 → CPU 飙升 → HighCPUUsage 告警。
 */
@Component
public class OpsTools {

    private static final String STORY_SERVICE = "data-sync-service";

    @Tool(description = "查询当前活跃的告警列表")
    public List<Alert> queryActiveAlerts() {
        return List.of(new Alert("HighCPUUsage", STORY_SERVICE, "critical", "2026-06-19T13:50:00Z"));
    }

    @Tool(description = "查询指定服务最近的 CPU 使用率指标(时间序列,百分比)")
    public List<CpuMetric> queryCpuMetrics(@ToolParam(description = "服务名") String service) {
        if (!STORY_SERVICE.equals(service)) {
            return List.of();
        }
        return List.of(
                new CpuMetric("2026-06-19T13:30:00Z", 42.0),
                new CpuMetric("2026-06-19T13:35:00Z", 58.0),
                new CpuMetric("2026-06-19T13:40:00Z", 71.0),
                new CpuMetric("2026-06-19T13:45:00Z", 85.0),
                new CpuMetric("2026-06-19T13:50:00Z", 94.0));
    }

    @Tool(description = "搜索指定服务最近的日志")
    public List<LogEntry> searchLogs(@ToolParam(description = "服务名") String service) {
        if (!STORY_SERVICE.equals(service)) {
            return List.of();
        }
        return List.of(
                new LogEntry(
                        "2026-06-19T13:42:00Z", "WARN", "Sync queue backlog growing: 1200 pending tasks"),
                new LogEntry("2026-06-19T13:46:00Z", "WARN", "Batch sync slow: last batch took 8500ms"),
                new LogEntry(
                        "2026-06-19T13:49:00Z", "ERROR", "Worker thread pool exhausted, sync tasks queuing"),
                new LogEntry(
                        "2026-06-19T13:50:00Z", "ERROR", "CPU saturation detected, sync throughput degraded"));
    }

    public record Alert(String name, String service, String severity, String timestamp) {}

    public record CpuMetric(String timestamp, double usagePercent) {}

    public record LogEntry(String timestamp, String level, String message) {}
}