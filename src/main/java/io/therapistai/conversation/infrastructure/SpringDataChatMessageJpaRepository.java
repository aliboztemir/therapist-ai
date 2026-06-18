package io.therapistai.conversation.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringDataChatMessageJpaRepository
        extends JpaRepository<ChatMessageEntity, UUID> {

    List<ChatMessageEntity> findByConversationIdOrderByMessageOrderAsc(
            UUID conversationId
    );

    List<ChatMessageEntity> findByConversationIdOrderByMessageOrderDesc(
            UUID conversationId,
            Pageable pageable
    );

    @Query("""
            select coalesce(max(m.messageOrder), -1) + 1
            from ChatMessageEntity m
            where m.conversationId = :conversationId
            """)
    int nextMessageOrder(UUID conversationId);

    long countByConversationId(UUID conversationId);
}