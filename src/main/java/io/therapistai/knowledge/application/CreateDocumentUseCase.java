package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.domain.DocumentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application-layer use case — no Spring annotations.
 * Wired as a Spring bean via KnowledgeConfiguration.
 */
public class CreateDocumentUseCase {

    private final DocumentRepository documentRepository;

    public CreateDocumentUseCase(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public UUID execute(CreateDocumentCommand command) {
        Document document = new Document(
                UUID.randomUUID(),
                command.title(),
                command.content(),
                command.source(),
                LocalDateTime.now()
        );
        documentRepository.save(document);
        return document.getId();
    }
}

