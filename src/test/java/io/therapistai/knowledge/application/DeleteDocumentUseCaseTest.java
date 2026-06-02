package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.infrastructure.InMemoryDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteDocumentUseCaseTest {

    private InMemoryDocumentRepository repository;
    private DeleteDocumentUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentRepository();
        useCase = new DeleteDocumentUseCase(repository);
    }

    @Test
    void execute_shouldReturnTrue_andRemoveDocument_whenExists() {
        Document saved = repository.save(buildDocument());

        boolean result = useCase.execute(saved.getId());

        assertTrue(result);
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void execute_shouldReturnFalse_whenDocumentDoesNotExist() {
        boolean result = useCase.execute(UUID.randomUUID());

        assertFalse(result);
    }

    @Test
    void execute_shouldNotAffectOtherDocuments_whenOneIsDeleted() {
        Document first  = repository.save(buildDocument());
        Document second = repository.save(buildDocument());

        useCase.execute(first.getId());

        assertTrue(repository.findById(first.getId()).isEmpty());
        assertTrue(repository.findById(second.getId()).isPresent());
    }

    private static Document buildDocument() {
        return new Document(UUID.randomUUID(), "Title", "Content", "Source", LocalDateTime.now());
    }
}

