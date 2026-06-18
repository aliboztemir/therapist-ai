package io.therapistai.conversation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository {

    ChatMessage save(ChatMessage message);

    List<ChatMessage> findRecentMessages(
            UUID conversationId,
            int limit
    );

    List<ChatMessage> findConversationHistory(
            UUID conversationId
    );

    int nextMessageOrder(UUID conversationId);

}