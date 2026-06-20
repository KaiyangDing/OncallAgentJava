package com.oncallagent.diagnosis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/** 规划器:把诊断目标拆成一步步可执行的计划(只规划,不执行)。 */
@Component
public class Planner {

    private static final String SYSTEM_PROMPT =
            """
            你是运维诊断规划器。把用户给的诊断目标拆解成一个有序的步骤列表。
            你只能依赖以下能力来设计步骤(不要臆造其它工具):
            - 查询当前活跃告警
            - 查询指定服务的 CPU 使用率指标
            - 搜索指定服务的日志
            - 检索运维知识库
            要求:步骤要具体、可执行、最少必要,通常 3-6 步。
            只输出 JSON,严格形如 {"steps": ["第一步", "第二步", "第三步"]},
            不要输出任何解释文字,也不要加 ```json 代码块标记。
            """;

    private final ChatClient chatClient;

    public Planner(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Plan plan(String goal) {
        return chatClient.prompt().system(SYSTEM_PROMPT).user(goal).call().entity(Plan.class);
    }
}
