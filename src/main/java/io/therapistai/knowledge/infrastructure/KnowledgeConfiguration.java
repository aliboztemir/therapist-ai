package io.therapistai.knowledge.infrastructure;

import io.therapistai.knowledge.application.CreateDocumentUseCase;
import io.therapistai.knowledge.application.DeleteDocumentUseCase;
import io.therapistai.knowledge.application.FindDocumentByIdUseCase;
import io.therapistai.knowledge.application.FindDocumentsUseCase;
import io.therapistai.knowledge.domain.DocumentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeConfiguration {

    @Bean
    public CreateDocumentUseCase createDocumentUseCase(DocumentRepository documentRepository) {
        return new CreateDocumentUseCase(documentRepository);
    }

    @Bean
    public FindDocumentsUseCase findDocumentsUseCase(DocumentRepository documentRepository) {
        return new FindDocumentsUseCase(documentRepository);
    }

    @Bean
    public FindDocumentByIdUseCase findDocumentByIdUseCase(DocumentRepository documentRepository) {
        return new FindDocumentByIdUseCase(documentRepository);
    }

    @Bean
    public DeleteDocumentUseCase deleteDocumentUseCase(DocumentRepository documentRepository) {
        return new DeleteDocumentUseCase(documentRepository);
    }
}

