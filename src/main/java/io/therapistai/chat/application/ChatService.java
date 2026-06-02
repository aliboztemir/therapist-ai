package io.therapistai.chat.application;

import io.therapistai.chat.api.ChatRequest;
import io.therapistai.chat.api.ChatResponse;
import io.therapistai.chat.domain.ChatMessage;
import io.therapistai.chat.domain.Conversation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final PromptComposer promptComposer;
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();

    public ChatService(ChatClient.Builder chatClientBuilder, PromptComposer promptComposer) {
        this.chatClient = chatClientBuilder.build();
        this.promptComposer = promptComposer;
    }

    public ChatResponse chat(ChatRequest request) {

        String conversationId = (request.conversationId() == null || request.conversationId().isBlank())
                ? UUID.randomUUID().toString()
                : request.conversationId();

        Conversation conversation = conversations.computeIfAbsent(conversationId, Conversation::new);

        List<Message> messages = buildMessages(conversation, request.message());

        String assistantReply = chatClient.prompt()
                .messages(messages)
                .options(OpenAiChatOptions.builder().maxTokens(300))
                .call()
                .content();

        conversation.addMessage(new ChatMessage(ChatMessage.Role.USER, request.message()));
        conversation.addMessage(new ChatMessage(ChatMessage.Role.ASSISTANT, assistantReply));

        return new ChatResponse(assistantReply, conversationId);
    }

    private List<Message> buildMessages(Conversation conversation, String currentUserMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(promptComposer.systemPrompt()));

        for (ChatMessage msg : conversation.getMessages()) {
            if (msg.role() == ChatMessage.Role.USER) {
                messages.add(new UserMessage(msg.content()));
            } else {
                messages.add(new AssistantMessage(msg.content()));
            }
        }

        messages.add(new UserMessage(currentUserMessage));
        return messages;
    }
}