package io.therapistai.memory.application;

import java.util.Objects;
import java.util.UUID;

public record MemoryRetrievalQuery(
        UUID userId,
        UUID conversationId,
        String currentMessage,
        MemorySignalContext signalContext,
        int limit
) {

    public static final int DEFAULT_LIMIT = 8;
    public static final int MAX_LIMIT = 20;

    public MemoryRetrievalQuery {
        Objects.requireNonNull(
                userId,
                "MemoryRetrievalQuery.userId must not be null"
        );

        currentMessage =
                currentMessage != null
                        ? currentMessage.strip()
                        : "";

        signalContext =
                signalContext != null
                        ? signalContext
                        : MemorySignalContext.empty();

        if (limit <= 0) {
            limit = DEFAULT_LIMIT;
        }

        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
    }
}