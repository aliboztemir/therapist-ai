package io.therapistai.knowledge.infrastructure;

import io.therapistai.knowledge.application.CreateDocumentUseCase;
import io.therapistai.knowledge.domain.DocumentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeConfiguration {

    /**
     * Wire the use case as a Spring bean without polluting the
     * application layer with @Service or any Spring annotation.
     */
    @Bean
    public CreateDocumentUseCase createDocumentUseCase(DocumentRepository documentRepository) {
        return new CreateDocumentUseCase(documentRepository);
    }
}

