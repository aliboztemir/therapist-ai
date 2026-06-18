package io.therapistai.coreflow;

import io.therapistai.analysis.domain.MessageAnalysis;
import io.therapistai.memory.domain.MemorySnapshot;
import io.therapistai.security.domain.UserProfile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record TurnContext(
        UUID messageId,
        UUID userId,
        UUID conversationId,
        boolean isNewConversation,
        UserProfile userProfile,
        String currentMessage,
        List<String> recentHistory,
        MessageAnalysis analysis,
        MemorySnapshot memorySnapshot
) {

    public TurnContext {
        Objects.requireNonNull(
                messageId,
                "messageId must not be null"
        );

        Objects.requireNonNull(
                userId,
                "userId must not be null"
        );

        Objects.requireNonNull(
                conversationId,
                "conversationId must not be null"
        );

        Objects.requireNonNull(
                userProfile,
                "userProfile must not be null"
        );

        currentMessage =
                currentMessage != null
                        ? currentMessage
                        : "";

        recentHistory =
                recentHistory != null
                        ? List.copyOf(recentHistory)
                        : List.of();

        analysis =
                analysis != null
                        ? analysis
                        : MessageAnalysis.safeDefault(
                        "turn-context-no-analysis"
                );

        memorySnapshot =
                memorySnapshot != null
                        ? memorySnapshot
                        : MemorySnapshot.empty();
    }

    public TurnContext withMemorySnapshot(
            MemorySnapshot snapshot
    ) {
        return new TurnContext(
                messageId,
                userId,
                conversationId,
                isNewConversation,
                userProfile,
                currentMessage,
                recentHistory,
                analysis,
                snapshot
        );
    }
}