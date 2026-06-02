package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.infrastructure.InMemoryDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FindDocumentByIdUseCaseTest {

    private InMemoryDocumentRepository repository;
    private FindDocumentByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentRepository();
        useCase = new FindDocumentByIdUseCase(repository);
    }

    @Test
    void execute_shouldReturnDocument_whenIdExists() {
        Document saved = repository.save(buildDocument());

        Optional<Document> result = useCase.execute(saved.getId());

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
        assertEquals(saved.getTitle(), result.get().getTitle());
    }

    @Test
    void execute_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<Document> result = useCase.execute(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void execute_shouldNotReturnDocumentFromDifferentId() {
        repository.save(buildDocument());

        Optional<Document> result = useCase.execute(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    private static Document buildDocument() {
        return new Document(UUID.randomUUID(), "Title", "Content", "Source", LocalDateTime.now());
    }
}

