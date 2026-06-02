package io.therapistai.chat.application;

import io.therapistai.chat.api.ChatRequest;
import io.therapistai.chat.api.ChatResponse;
import org.springframework.stereotype.Service;

/**
 * Application service (use-case layer) for chat operations.
 * Currently contains placeholder logic. Will be extended with RAG/LLM later.
 */
@Service
public class ChatService {

    /**
     * Process an incoming chat request and produce a ChatResponse.
     * Simple placeholder implementation (no LLM/RAG).
     */
    public ChatResponse process(ChatRequest request) {
        String conversationId = java.util.UUID.randomUUID().toString();
        String answer = "Can you tell me more about that feeling?";
        return new ChatResponse(answer, conversationId);
    }
}
