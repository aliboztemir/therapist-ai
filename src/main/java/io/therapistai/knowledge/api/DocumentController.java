package io.therapistai.knowledge.api;

import io.therapistai.knowledge.application.CreateDocumentCommand;
import io.therapistai.knowledge.application.CreateDocumentUseCase;
import io.therapistai.knowledge.application.DeleteDocumentUseCase;
import io.therapistai.knowledge.application.FindDocumentByIdUseCase;
import io.therapistai.knowledge.application.FindDocumentsUseCase;
import io.therapistai.knowledge.domain.Document;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;
    private final FindDocumentsUseCase findDocumentsUseCase;
    private final FindDocumentByIdUseCase findDocumentByIdUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;

    public DocumentController(
            CreateDocumentUseCase createDocumentUseCase,
            FindDocumentsUseCase findDocumentsUseCase,
            FindDocumentByIdUseCase findDocumentByIdUseCase,
            DeleteDocumentUseCase deleteDocumentUseCase) {
        this.createDocumentUseCase = createDocumentUseCase;
        this.findDocumentsUseCase = findDocumentsUseCase;
        this.findDocumentByIdUseCase = findDocumentByIdUseCase;
        this.deleteDocumentUseCase = deleteDocumentUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateDocumentResponse> create(
            @Valid @RequestBody CreateDocumentRequest request) {

        UUID documentId = createDocumentUseCase.execute(
                new CreateDocumentCommand(request.title(), request.content(), request.source())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateDocumentResponse(documentId));
    }

    @GetMapping
    public ResponseEntity<List<DocumentSummaryResponse>> findAll() {
        List<DocumentSummaryResponse> response = findDocumentsUseCase.execute()
                .stream()
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentSummaryResponse> findById(@PathVariable UUID id) {
        return findDocumentByIdUseCase.execute(id)
                .map(doc -> ResponseEntity.ok(toSummary(doc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = deleteDocumentUseCase.execute(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private DocumentSummaryResponse toSummary(Document doc) {
        return new DocumentSummaryResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getContent(),
                doc.getSource(),
                doc.getCreatedAt()
        );
    }
}
