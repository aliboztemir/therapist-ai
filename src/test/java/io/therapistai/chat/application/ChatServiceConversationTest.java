package io.therapistai.chat.application;

import io.therapistai.chat.api.ChatRequest;
import io.therapistai.chat.api.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

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
    private PromptComposer promptComposer;

    @Captor
    private ArgumentCaptor<List<Message>> messagesCaptor;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(promptComposer.systemPrompt()).thenReturn("You are a therapist assistant.");
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.messages(any(java.util.List.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);

        chatService = new ChatService(chatClientBuilder, promptComposer);
    }

    @Test
    void newConversation_shouldGenerateConversationId_whenConversationIdIsNull() {
        when(callResponseSpec.content()).thenReturn("Hello, I'm here to help.");

        ChatRequest request = new ChatRequest("Hello", null, null, null);
        ChatResponse response = chatService.chat(request);

        assertNotNull(response.conversationId(), "conversationId must not be null");
        assertFalse(response.conversationId().isBlank(), "conversationId must not be blank");
        assertEquals("Hello, I'm here to help.", response.answer());
    }

    @Test
    void newConversation_shouldGenerateConversationId_whenConversationIdIsBlank() {
        when(callResponseSpec.content()).thenReturn("Hello, I'm here to help.");

        ChatRequest request = new ChatRequest("Hello", null, "   ", null);
        ChatResponse response = chatService.chat(request);

        assertNotNull(response.conversationId());
        assertFalse(response.conversationId().isBlank());
    }

    @Test
    void existingConversation_shouldReuseConversationId() {
        when(callResponseSpec.content()).thenReturn("First reply", "Second reply");

        ChatRequest firstRequest = new ChatRequest("How are you?", null, null, null);
        ChatResponse firstResponse = chatService.chat(firstRequest);

        String existingId = firstResponse.conversationId();

        ChatRequest secondRequest = new ChatRequest("Tell me more.", null, existingId, null);
        ChatResponse secondResponse = chatService.chat(secondRequest);

        assertEquals(existingId, secondResponse.conversationId(),
                "Second response must return the same conversationId");
    }

    @Test
    void differentRequests_withNoConversationId_shouldGetDifferentConversationIds() {
        when(callResponseSpec.content()).thenReturn("Reply A", "Reply B");

        ChatResponse responseA = chatService.chat(new ChatRequest("Message A", null, null, null));
        ChatResponse responseB = chatService.chat(new ChatRequest("Message B", null, null, null));

        assertNotEquals(responseA.conversationId(), responseB.conversationId(),
                "Two independent requests should produce different conversationIds");
    }

    @Test
    void existingConversation_shouldPassHistoryToChatClient() {
        when(callResponseSpec.content()).thenReturn("First reply", "Second reply");

        // First exchange — establishes history
        ChatResponse firstResponse = chatService.chat(new ChatRequest("First message", null, null, null));
        String conversationId = firstResponse.conversationId();

        // Second exchange — history must be included in the messages sent to ChatClient
        chatService.chat(new ChatRequest("Second message", null, conversationId, null));

        verify(requestSpec, times(2)).messages(messagesCaptor.capture());
        List<Message> secondCallMessages = messagesCaptor.getAllValues().get(1);

        // Expected order: [SystemMessage, UserMessage(history), AssistantMessage(history), UserMessage(current)]
        assertEquals(4, secondCallMessages.size(), "Second call must include system + 2 history messages + current user message");

        assertInstanceOf(SystemMessage.class,    secondCallMessages.get(0), "First message must be the system prompt");
        assertInstanceOf(UserMessage.class,      secondCallMessages.get(1), "Second message must be the historical user turn");
        assertInstanceOf(AssistantMessage.class, secondCallMessages.get(2), "Third message must be the historical assistant turn");
        assertInstanceOf(UserMessage.class,      secondCallMessages.get(3), "Fourth message must be the current user message");

        assertEquals("First message",  secondCallMessages.get(1).getText(), "Historical user message content must match");
        assertEquals("First reply",    secondCallMessages.get(2).getText(), "Historical assistant reply content must match");
        assertEquals("Second message", secondCallMessages.get(3).getText(), "Current user message content must match");
    }
}
