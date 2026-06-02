package io.therapistai.knowledge.infrastructure;

import io.therapistai.knowledge.domain.Document;
import io.therapistai.knowledge.domain.DocumentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDocumentRepository implements DocumentRepository {

    private final Map<UUID, Document> store = new ConcurrentHashMap<>();

    @Override
    public Document save(Document document) {
        store.put(document.getId(), document);
        return document;
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Document> findAll() {
        return List.copyOf(store.values());
    }
}

