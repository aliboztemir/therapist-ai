package io.therapistai.knowledge.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Document {

    private final UUID id;
    private final String title;
    private final String content;
    private final String source;
    private final LocalDateTime createdAt;

    public Document(UUID id, String title, String content, String source, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.createdAt = createdAt;
    }

    public UUID getId()                  { return id; }
    public String getTitle()             { return title; }
    public String getContent()           { return content; }
    public String getSource()            { return source; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    // Entity equality is based on identity, not value
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document d)) return false;
        return id.equals(d.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

