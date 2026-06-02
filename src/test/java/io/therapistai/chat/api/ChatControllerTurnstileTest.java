package io.therapistai.chat.api;

import io.therapistai.chat.application.ChatService;
import io.therapistai.security.turnstile.TurnstileVerificationException;
import io.therapistai.security.turnstile.TurnstileVerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTurnstileTest {

    @Mock
    private ChatService chatService;

    @Mock
    private TurnstileVerificationService turnstileVerificationService;

    @InjectMocks
    private ChatController chatController;

    @Test
    void chat_validToken_shouldCallChatService() {
        when(turnstileVerificationService.verify("valid-token")).thenReturn(true);
        when(chatService.chat(any())).thenReturn(new ChatResponse("Hello!", "conv-1"));

        ChatRequest request = new ChatRequest("Hello", null, null, "valid-token");
        var response = chatController.chat(request);

        assertEquals(200, response.getStatusCode().value());
        verify(chatService, times(1)).chat(request);
    }

    @Test
    void chat_invalidToken_shouldThrowAndNotCallOpenAI() {
        when(turnstileVerificationService.verify("bad-token")).thenReturn(false);

        ChatRequest request = new ChatRequest("Hello", null, null, "bad-token");

        assertThrows(TurnstileVerificationException.class, () -> chatController.chat(request));
        verify(chatService, never()).chat(any());
    }

    @Test
    void chat_missingToken_shouldThrowAndNotCallOpenAI() {
        when(turnstileVerificationService.verify(null)).thenReturn(false);

        ChatRequest request = new ChatRequest("Hello", null, null, null);

        assertThrows(TurnstileVerificationException.class, () -> chatController.chat(request));
        verify(chatService, never()).chat(any());
    }
}

