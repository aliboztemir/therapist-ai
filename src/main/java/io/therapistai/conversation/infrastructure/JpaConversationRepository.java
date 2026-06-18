package io.therapistai.conversation.infrastructure;

import io.therapistai.conversation.domain.Conversation;
import io.therapistai.conversation.domain.ConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaConversationRepository implements ConversationRepository {

    private final SpringDataConversationJpaRepository jpaRepository;

    public JpaConversationRepository(
            SpringDataConversationJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Conversation> findById(UUID conversationId) {
        return jpaRepository
                .findById(conversationId)
                .map(this::toDomain);
    }

    @Override
    public List<Conversation> findByUserId(UUID userId) {
        return jpaRepository
                .findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Conversation save(Conversation conversation) {
        ConversationEntity entity = new ConversationEntity(
                conversation.id(),
                conversation.userId(),
                conversation.createdAt(),
                conversation.updatedAt()
        );

        ConversationEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public boolean exists(UUID conversationId) {
        return jpaRepository.existsById(conversationId);
    }

    @Override
    public boolean belongsToUser(
            UUID conversationId,
            UUID userId
    ) {
        return jpaRepository.existsByUserIdAndId(
                userId,
                conversationId
        );
    }

    private Conversation toDomain(ConversationEntity entity) {
        return new Conversation(
                entity.getId(),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}