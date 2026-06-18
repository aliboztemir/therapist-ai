package io.therapistai.coreflow;

import io.therapistai.conversation.api.ChatRequest;
import io.therapistai.conversation.api.ChatResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CoreflowChatUseCase {

    private final CoreflowOrchestrator orchestrator;

    public CoreflowChatUseCase(
            CoreflowOrchestrator orchestrator
    ) {
        this.orchestrator = orchestrator;
    }

    public ChatResponse chat(
            Authentication authentication,
            ChatRequest request
    ) {
        return orchestrator.chat(
                new CoreflowChatCommand(
                        authentication,
                        request
                )
        );
    }

    public Flux<String> stream(
            Authentication authentication,
            ChatRequest request
    ) {
        return Flux.just(
                chat(authentication, request).answer()
        );
    }
}