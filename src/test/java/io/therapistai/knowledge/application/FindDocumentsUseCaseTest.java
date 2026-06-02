package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.infrastructure.InMemoryDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FindDocumentsUseCaseTest {

    private InMemoryDocumentRepository repository;
    private FindDocumentsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentRepository();
        useCase = new FindDocumentsUseCase(repository);
    }

    @Test
    void execute_shouldReturnEmptyList_whenNoDocumentsExist() {
        List<Document> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void execute_shouldReturnAllSavedDocuments() {
        repository.save(buildDocument("First"));
        repository.save(buildDocument("Second"));

        List<Document> result = useCase.execute();

        assertEquals(2, result.size());
    }

    @Test
    void execute_shouldReturnSingleDocument_afterOneIsSaved() {
        Document saved = repository.save(buildDocument("Solo"));

        List<Document> result = useCase.execute();

        assertEquals(1, result.size());
        assertEquals(saved.getId(), result.get(0).getId());
    }

    private static Document buildDocument(String title) {
        return new Document(UUID.randomUUID(), title, "Content", "Source", LocalDateTime.now());
    }
}

