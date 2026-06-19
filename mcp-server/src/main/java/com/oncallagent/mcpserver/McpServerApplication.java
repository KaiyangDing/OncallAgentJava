package com.oncallagent.mcpserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	/** 把 OpsTools 的 @Tool 方法暴露成 MCP 工具。 */
	@Bean
	public ToolCallbackProvider opsToolCallbacks(OpsTools opsTools) {
		return MethodToolCallbackProvider.builder().toolObjects(opsTools).build();
	}
}