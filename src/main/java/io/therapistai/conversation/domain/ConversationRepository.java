package io.therapistai.conversation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository {

    Optional<Conversation> findById(UUID conversationId);

    List<Conversation> findByUserId(UUID userId);

    Conversation save(Conversation conversation);

    boolean exists(UUID conversationId);

    boolean belongsToUser(
            UUID conversationId,
            UUID userId
    );
}