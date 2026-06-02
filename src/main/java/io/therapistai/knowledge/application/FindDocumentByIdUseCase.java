package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.domain.DocumentRepository;

import java.util.Optional;
import java.util.UUID;

public class FindDocumentByIdUseCase {

    private final DocumentRepository documentRepository;

    public FindDocumentByIdUseCase(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Optional<Document> execute(UUID id) {
        return documentRepository.findById(id);
    }
}

