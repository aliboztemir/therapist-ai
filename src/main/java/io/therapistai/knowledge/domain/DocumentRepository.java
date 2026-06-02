package io.therapistai.knowledge.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {

    Document save(Document document);

    Optional<Document> findById(UUID id);

    List<Document> findAll();

    void delete(UUID id);
}

