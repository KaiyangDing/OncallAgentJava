package com.oncallagent.chat;

import com.oncallagent.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/** 对话接口。 */
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /** 阻塞式:一次返回完整答案(统一信封)。 */
    @PostMapping("/api/chat")
    public ApiResponse<ChatReply> chat(@RequestBody ChatRequest request) {
        String answer = chatService.chat(conversationId(request), request.message());
        return ApiResponse.ok(new ChatReply(answer));
    }

    /** 流式:SSE 逐块返回(SSE 自有格式,不套信封)。 */
    @PostMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return chatService.chatStream(conversationId(request), request.message());
    }

    private String conversationId(ChatRequest request) {
        return StringUtils.hasText(request.conversationId()) ? request.conversationId() : "default";
    }

    public record ChatRequest(String conversationId, String message) {}

    public record ChatReply(String reply) {}
}
