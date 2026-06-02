package io.therapistai.knowledge.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentSummaryResponse(

        UUID id,
        String title,
        String content,
        String source,
        LocalDateTime createdAt

) {
}

