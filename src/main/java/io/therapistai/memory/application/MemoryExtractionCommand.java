package io.therapistai.memory.application;

import java.util.UUID;

public record MemoryExtractionCommand(
        UUID userId,
        UUID conversationId,
        UUID messageId,
        String currentMessage,
        MemorySignalContext signalContext) {
}