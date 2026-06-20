package com.oncallagent.diagnosis;

import com.oncallagent.chat.KnowledgeTools;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

/** 执行器:执行计划中的单个步骤,内置工具循环(知识检索 + MCP 运维工具)。 */
@Component
public class Executor {

    private static final String SYSTEM_PROMPT =
            """
            你是运维诊断执行器。根据"总目标"和"已完成步骤"的上下文,完成"当前步骤"。
            你有这些工具:查活跃告警、查指定服务的CPU指标、搜指定服务的日志、检索运维知识库。
            调用合适的工具完成当前步骤,然后用简洁中文总结这一步的关键发现(包含查到的具体数据)。
            """;

    private final ChatClient chatClient;

    public Executor(
            ChatClient.Builder chatClientBuilder,
            KnowledgeTools knowledgeTools,
            ToolCallbackProvider mcpToolCallbackProvider) {
        this.chatClient =
                chatClientBuilder
                        .defaultSystem(SYSTEM_PROMPT)
                        .defaultTools(knowledgeTools)
                        .defaultToolCallbacks(mcpToolCallbackProvider.getToolCallbacks())
                        .build();
    }

    public String execute(String goal, String step, List<PastStep> pastSteps) {
        return chatClient.prompt().user(buildContext(goal, step, pastSteps)).call().content();
    }

    private String buildContext(String goal, String step, List<PastStep> pastSteps) {
        StringBuilder sb = new StringBuilder();
        sb.append("总目标: ").append(goal).append("\n\n");
        if (!pastSteps.isEmpty()) {
            sb.append("已完成步骤及发现:\n");
            for (PastStep ps : pastSteps) {
                sb.append("- ").append(ps.step()).append(" → ").append(ps.result()).append("\n");
            }
            sb.append("\n");
        }
        sb.append("当前要执行的步骤: ").append(step);
        return sb.toString();
    }
}
