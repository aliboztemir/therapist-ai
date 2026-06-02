package io.therapistai.knowledge.application;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.domain.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateDocumentUseCaseTest {

    private CapturingDocumentRepository repository;
    private CreateDocumentUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new CapturingDocumentRepository();
        useCase = new CreateDocumentUseCase(repository);
    }

    @Test
    void execute_shouldReturnNonNullUUID() {
        CreateDocumentCommand command = new CreateDocumentCommand("Title", "Content", "Source");

        UUID id = useCase.execute(command);

        assertNotNull(id);
    }

    @Test
    void execute_shouldPersistDocumentWithCorrectFields() {
        CreateDocumentCommand command = new CreateDocumentCommand("My Title", "My Content", "https://source.example.com");

        UUID id = useCase.execute(command);

        assertEquals(1, repository.saved.size());
        Document saved = repository.saved.get(0);
        assertEquals(id,                saved.getId());
        assertEquals("My Title",        saved.getTitle());
        assertEquals("My Content",      saved.getContent());
        assertEquals("https://source.example.com", saved.getSource());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void execute_shouldGenerateUniqueIdsForDifferentDocuments() {
        UUID id1 = useCase.execute(new CreateDocumentCommand("T1", "C1", null));
        UUID id2 = useCase.execute(new CreateDocumentCommand("T2", "C2", null));

        assertNotEquals(id1, id2);
    }

    @Test
    void execute_shouldAllowNullSource() {
        CreateDocumentCommand command = new CreateDocumentCommand("Title", "Content", null);

        UUID id = useCase.execute(command);

        assertNotNull(id);
        assertNull(repository.saved.get(0).getSource());
    }

    // ── Minimal in-memory stub — no Mockito needed ──────────────────────────
    static class CapturingDocumentRepository implements DocumentRepository {

        final List<Document> saved = new ArrayList<>();

        @Override
        public Document save(Document document) {
            saved.add(document);
            return document;
        }

        @Override
        public Optional<Document> findById(UUID id) {
            return saved.stream().filter(d -> d.getId().equals(id)).findFirst();
        }

        @Override
        public List<Document> findAll() {
            return List.copyOf(saved);
        }
    }
}

