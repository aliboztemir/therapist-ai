package io.therapistai.chat.application;

import io.therapistai.chat.api.ChatRequest;
import io.therapistai.chat.api.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            You are an empathetic, psychotherapy-inspired conversational assistant.
            You are not a licensed therapist and must not provide medical diagnoses.
            Provide supportive, reflective, and exploratory responses.
            Encourage users to elaborate on feelings.
            If the user indicates risk of harm, advise them to seek immediate professional help or emergency services.
            Keep answers concise and respectful.
            """;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public ChatResponse chat(ChatRequest request) {

        String assistantReply = chatClient.prompt()
                .user(request.message())
                .call()
                .content();

        return new ChatResponse(
                assistantReply,
                UUID.randomUUID().toString()
        );
    }
}