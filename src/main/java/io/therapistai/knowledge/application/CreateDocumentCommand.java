package io.therapistai.knowledge.application;

public record CreateDocumentCommand(

        String title,

        String content,

        String source

) {
}

