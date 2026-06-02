package io.therapistai.knowledge.infrastructure;

import io.therapistai.knowledge.domain.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDocumentRepositoryTest {

    private InMemoryDocumentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentRepository();
    }

    @Test
    void save_shouldReturnTheSameDocument() {
        Document document = buildDocument();

        Document result = repository.save(document);

        assertSame(document, result);
    }

    @Test
    void findById_shouldReturnDocument_whenExists() {
        Document document = buildDocument();
        repository.save(document);

        Optional<Document> found = repository.findById(document.getId());

        assertTrue(found.isPresent());
        assertEquals(document.getId(), found.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Document> found = repository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllSavedDocuments() {
        repository.save(buildDocument());
        repository.save(buildDocument());

        List<Document> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void save_shouldOverwriteDocumentWithSameId() {
        UUID id = UUID.randomUUID();
        Document first  = new Document(id, "First",  "Content A", null, LocalDateTime.now());
        Document second = new Document(id, "Second", "Content B", null, LocalDateTime.now());

        repository.save(first);
        repository.save(second);

        assertEquals(1, repository.findAll().size());
        assertEquals("Second", repository.findById(id).get().getTitle());
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private static Document buildDocument() {
        return new Document(UUID.randomUUID(), "Title", "Content", "Source", LocalDateTime.now());
    }
}

