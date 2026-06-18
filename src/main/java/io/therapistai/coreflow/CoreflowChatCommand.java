package io.therapistai.coreflow;

import io.therapistai.conversation.api.ChatRequest;
import org.springframework.security.core.Authentication;

import java.util.Objects;

public record CoreflowChatCommand(
        Authentication authentication,
        ChatRequest request
) {

    public CoreflowChatCommand {
        Objects.requireNonNull(authentication, "authentication must not be null");
        Objects.requireNonNull(request, "request must not be null");
    }
}