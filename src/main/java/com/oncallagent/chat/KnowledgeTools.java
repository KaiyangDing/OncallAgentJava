package com.oncallagent.chat;

import com.oncallagent.knowledge.KnowledgeRetriever;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/** 暴露给对话 Agent 的工具:检索运维知识库。 */
@Component
public class KnowledgeTools {
    private final KnowledgeRetriever retriever;

    public KnowledgeTools(KnowledgeRetriever retriever) {
        this.retriever = retriever;
    }

    @Tool(description = "检索运维知识库,根据问题返回最相关的知识片段。" + "当用户问题涉及运维知识、故障排查、告警处理时调用。")
    public String searchKnowledgeBase(String query) {
        List<Document> docs = retriever.retrieve(query);
        if (docs.isEmpty()) {
            return "知识库中没有找到与该问题相关的内容。";
        }
        return docs.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));
    }
}
