package com.oncallagent.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/** RAG 对话服务:知识检索工具 + 多轮记忆,支持阻塞与流式两种输出。 */
@Service
public class ChatService {

    private static final String SYSTEM_PROMPT =
            """
            你是一个运维 OnCall 助手。回答用户问题时,先用 searchKnowledgeBase 工具检索知识库,
            基于检索到的内容作答。如果知识库没有相关内容,如实告知你不知道,不要编造。
            """;

    private final ChatClient chatClient;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            KnowledgeTools knowledgeTools,
            ChatMemory chatMemory) {
        this.chatClient =
                chatClientBuilder
                        .defaultSystem(SYSTEM_PROMPT)
                        .defaultTools(knowledgeTools)
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .build();
    }

    public String chat(String conversationId, String userMessage) {
        return chatClient
                .prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    public Flux<String> chatStream(String conversationId, String userMessage) {
        return chatClient
                .prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }
}
