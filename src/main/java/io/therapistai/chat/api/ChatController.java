package io.therapistai.chat.api;

import io.therapistai.chat.application.ChatService;
import io.therapistai.security.TurnstileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final TurnstileService turnstileService;

    public ChatController(ChatService chatService, TurnstileService turnstileService) {
        this.chatService = chatService;
        this.turnstileService = turnstileService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        if (!turnstileService.verify(request.turnstileToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Captcha verification failed.");
        }
        return ResponseEntity.ok(chatService.chat(request));
    }
}