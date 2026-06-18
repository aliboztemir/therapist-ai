package io.therapistai.conversation.application;

import io.therapistai.conversation.domain.Conversation;
import io.therapistai.conversation.domain.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConversationQueryService {

    private final ConversationRepository conversationRepository;

    public ConversationQueryService(
            ConversationRepository conversationRepository
    ) {
        this.conversationRepository = conversationRepository;
    }

    public List<Conversation> findUserConversations(UUID userId) {

        Objects.requireNonNull(
                userId,
                "userId must not be null"
        );

        return conversationRepository.findByUserId(userId);
    }
}