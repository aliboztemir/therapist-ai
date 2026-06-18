package io.therapistai.conversation.infrastructure;

import io.therapistai.conversation.domain.ChatMessage;
import io.therapistai.conversation.domain.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaChatMessageRepository implements ChatMessageRepository {

    private final SpringDataChatMessageJpaRepository jpaRepository;

    public JpaChatMessageRepository(
            SpringDataChatMessageJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ChatMessage save(ChatMessage message) {
        ChatMessageEntity entity = new ChatMessageEntity(
                message.id(),
                message.conversationId(),
                message.userId(),
                message.role(),
                message.content(),
                message.messageOrder(),
                message.createdAt()
        );

        ChatMessageEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<ChatMessage> findById(UUID messageId) {
        return jpaRepository
                .findById(messageId)
                .map(this::toDomain);
    }

    @Override
    public List<ChatMessage> findRecentMessages(
            UUID conversationId,
            int limit
    ) {
        return jpaRepository
                .findByConversationIdOrderByMessageOrderDesc(
                        conversationId,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(this::toDomain)
                .sorted(Comparator.comparingInt(ChatMessage::messageOrder))
                .toList();
    }

    @Override
    public List<ChatMessage> findConversationHistory(UUID conversationId) {
        return jpaRepository
                .findByConversationIdOrderByMessageOrderAsc(conversationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public int nextMessageOrder(UUID conversationId) {
        return jpaRepository.nextMessageOrder(conversationId);
    }

    @Override
    public long countByConversationId(UUID conversationId) {
        return jpaRepository.countByConversationId(conversationId);
    }

    private ChatMessage toDomain(ChatMessageEntity entity) {
        return new ChatMessage(
                entity.getId(),
                entity.getConversationId(),
                entity.getUserId(),
                entity.getRole(),
                entity.getContent(),
                entity.getMessageOrder(),
                entity.getCreatedAt()
        );
    }
}