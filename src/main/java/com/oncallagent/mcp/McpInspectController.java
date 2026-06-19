package com.oncallagent.mcp;

import com.oncallagent.common.ApiResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 验证 MCP 客户端:列出从远程 MCP server 发现的工具,并实际调用一个。 */
@RestController
public class McpInspectController {

    private final ToolCallbackProvider mcpToolCallbackProvider;

    public McpInspectController(ToolCallbackProvider mcpToolCallbackProvider) {
        this.mcpToolCallbackProvider = mcpToolCallbackProvider;
    }

    /** 列出发现的远程 MCP 工具名。 */
    @GetMapping("/api/mcp/tools")
    public ApiResponse<List<String>> tools() {
        List<String> names =
                Arrays.stream(mcpToolCallbackProvider.getToolCallbacks())
                        .map(tc -> tc.getToolDefinition().name())
                        .toList();
        return ApiResponse.ok(names);
    }

    /** 跨进程实际调用 queryActiveAlerts,返回原始结果。 */
    @GetMapping("/api/mcp/alerts")
    public ApiResponse<String> alerts() {
        ToolCallback alerts =
                Arrays.stream(mcpToolCallbackProvider.getToolCallbacks())
                        .filter(tc -> tc.getToolDefinition().name().contains("queryActiveAlerts"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("未发现 queryActiveAlerts 工具"));
        return ApiResponse.ok(alerts.call("{}"));
    }
}
