package com.oncallagent.diagnosis;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/** 重规划器:看已完成步骤,决定收尾(出报告)还是继续(给剩余步骤)。 */
@Component
public class Replanner {

    private static final String SYSTEM_PROMPT =
            """
            你是运维诊断重规划器。根据"总目标"和"已完成步骤及发现",做结构化决策:
            - 若信息已足够定位根因:finished=true,在 report 写 Markdown 报告
              (含 ## 根因 / ## 依据 / ## 处置建议),remainingSteps 留空。
            - 若还需更多信息:finished=false,在 remainingSteps 给后续步骤
              (不要重复已完成的),report 留空。
            后续步骤只能用:查活跃告警、查服务CPU指标、搜服务日志、检索知识库。
            """;

    private final ChatClient chatClient;

    public Replanner(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ReplanDecision replan(String goal, List<PastStep> pastSteps) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(context(goal, pastSteps))
                .call()
                .entity(ReplanDecision.class);
    }

    private String context(String goal, List<PastStep> pastSteps) {
        StringBuilder sb = new StringBuilder("总目标: ").append(goal).append("\n\n已完成步骤及发现:\n");
        for (PastStep ps : pastSteps) {
            sb.append("- ").append(ps.step()).append(" → ").append(ps.result()).append("\n");
        }
        return sb.toString();
    }
}
