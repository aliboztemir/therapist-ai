package io.therapistai.conversation.api;

import io.therapistai.coreflow.CoreflowChatUseCase;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final CoreflowChatUseCase coreflowChat;

    public ChatController(CoreflowChatUseCase coreflowChat) {
        this.coreflowChat = coreflowChat;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            Authentication authentication,
            @Valid @RequestBody ChatRequest request
    ) {
        ChatResponse response = coreflowChat.chat(authentication, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> stream(
            Authentication authentication,
            @Valid @RequestBody ChatRequest request
    ) {
        return coreflowChat
                .stream(authentication, request)
                .concatWith(Flux.just("[DONE]"));
    }
}