package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.domain.DocumentRepository;

import java.util.List;

public class FindDocumentsUseCase {

    private final DocumentRepository documentRepository;

    public FindDocumentsUseCase(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> execute() {
        return documentRepository.findAll();
    }
}

