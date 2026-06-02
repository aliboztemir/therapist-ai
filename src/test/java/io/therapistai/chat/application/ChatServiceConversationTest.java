package io.therapistai.chat.application;

import io.therapistai.chat.api.ChatRequest;
import io.therapistai.chat.api.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceConversationTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private Resource systemPromptResource;

    private ChatService chatService;

    @BeforeEach
    void setUp() throws IOException {
        when(systemPromptResource.getContentAsString(StandardCharsets.UTF_8))
                .thenReturn("You are a therapist assistant.");
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.messages(any(java.util.List.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);

        chatService = new ChatService(chatClientBuilder, systemPromptResource);
    }

    @Test
    void newConversation_shouldGenerateConversationId_whenConversationIdIsNull() {
        when(callResponseSpec.content()).thenReturn("Hello, I'm here to help.");

        ChatRequest request = new ChatRequest("Hello", null, null);
        ChatResponse response = chatService.chat(request);

        assertNotNull(response.conversationId(), "conversationId must not be null");
        assertFalse(response.conversationId().isBlank(), "conversationId must not be blank");
        assertEquals("Hello, I'm here to help.", response.answer());
    }

    @Test
    void newConversation_shouldGenerateConversationId_whenConversationIdIsBlank() {
        when(callResponseSpec.content()).thenReturn("Hello, I'm here to help.");

        ChatRequest request = new ChatRequest("Hello", null, "   ");
        ChatResponse response = chatService.chat(request);

        assertNotNull(response.conversationId());
        assertFalse(response.conversationId().isBlank());
    }

    @Test
    void existingConversation_shouldReuseConversationId() {
        when(callResponseSpec.content()).thenReturn("First reply", "Second reply");

        ChatRequest firstRequest = new ChatRequest("How are you?", null, null);
        ChatResponse firstResponse = chatService.chat(firstRequest);

        String existingId = firstResponse.conversationId();

        ChatRequest secondRequest = new ChatRequest("Tell me more.", null, existingId);
        ChatResponse secondResponse = chatService.chat(secondRequest);

        assertEquals(existingId, secondResponse.conversationId(),
                "Second response must return the same conversationId");
    }

    @Test
    void differentRequests_withNoConversationId_shouldGetDifferentConversationIds() {
        when(callResponseSpec.content()).thenReturn("Reply A", "Reply B");

        ChatResponse responseA = chatService.chat(new ChatRequest("Message A", null, null));
        ChatResponse responseB = chatService.chat(new ChatRequest("Message B", null, null));

        assertNotEquals(responseA.conversationId(), responseB.conversationId(),
                "Two independent requests should produce different conversationIds");
    }
}

