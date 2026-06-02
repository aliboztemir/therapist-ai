package io.therapistai.knowledge.api;

import jakarta.validation.constraints.NotBlank;

public record CreateDocumentRequest(

        @NotBlank(message = "title must not be blank")
        String title,

        @NotBlank(message = "content must not be blank")
        String content,

        String source

) {
}

