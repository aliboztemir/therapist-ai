package io.therapistai.knowledge.api;

import io.therapistai.knowledge.application.CreateDocumentCommand;
import io.therapistai.knowledge.application.CreateDocumentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;

    public DocumentController(CreateDocumentUseCase createDocumentUseCase) {
        this.createDocumentUseCase = createDocumentUseCase;
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
}

