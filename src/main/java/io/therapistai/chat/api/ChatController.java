package io.therapistai.chat.api;

import io.therapistai.chat.application.ChatService;
import io.therapistai.security.turnstile.TurnstileVerificationException;
import io.therapistai.security.turnstile.TurnstileVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final TurnstileVerificationService turnstileVerificationService;

    public ChatController(ChatService chatService, TurnstileVerificationService turnstileVerificationService) {
        this.chatService = chatService;
        this.turnstileVerificationService = turnstileVerificationService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        if (!turnstileVerificationService.verify(request.turnstileToken())) {
            throw new TurnstileVerificationException("Turnstile verification failed.");
        }
        return ResponseEntity.ok(chatService.chat(request));
    }
}