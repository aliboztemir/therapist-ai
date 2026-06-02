package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.DocumentRepository;

import java.util.UUID;

public class DeleteDocumentUseCase {

    private final DocumentRepository documentRepository;

    public DeleteDocumentUseCase(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * @return true if the document existed and was deleted, false if not found.
     */
    public boolean execute(UUID id) {
        if (documentRepository.findById(id).isEmpty()) {
            return false;
        }
        documentRepository.delete(id);
        return true;
    }
}

