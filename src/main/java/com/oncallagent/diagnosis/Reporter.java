package com.oncallagent.diagnosis;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/** 报告器:基于已有发现强制产出 Markdown 报告(达最大步数时兜底)。 */
@Component
public class Reporter {

    private static final String SYSTEM_PROMPT =
            """
            你是运维诊断报告器。基于"总目标"和"已收集的发现",直接产出 Markdown 报告,
            含 ## 根因 / ## 依据 / ## 处置建议。即使信息不全,也基于已有内容给最佳判断。
            """;

    private final ChatClient chatClient;

    public Reporter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String report(String goal, List<PastStep> pastSteps) {
        StringBuilder sb = new StringBuilder("总目标: ").append(goal).append("\n\n已收集的发现:\n");
        for (PastStep ps : pastSteps) {
            sb.append("- ").append(ps.step()).append(" → ").append(ps.result()).append("\n");
        }
        return chatClient.prompt().system(SYSTEM_PROMPT).user(sb.toString()).call().content();
    }
}
