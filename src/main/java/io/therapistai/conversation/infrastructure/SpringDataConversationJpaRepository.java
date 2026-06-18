package io.therapistai.conversation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataConversationJpaRepository
        extends JpaRepository<ConversationEntity, UUID> {

    boolean existsByUserIdAndId(
            UUID userId,
            UUID conversationId
    );

    List<ConversationEntity> findByUserIdOrderByUpdatedAtDesc(
            UUID userId
    );
}