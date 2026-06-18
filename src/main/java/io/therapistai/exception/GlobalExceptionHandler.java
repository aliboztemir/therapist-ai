package io.therapistai.exception;

import io.therapistai.conversation.application.InvalidChatMessageException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_CHAT_MESSAGE =
            "Mesajınızı anlayamadım. Daha açık yazar mısınız?";

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                List.of(exception.getMessage()),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> messages = extractValidationMessages(exception);

        if (isChatEndpoint(request)) {
            messages = List.of(INVALID_CHAT_MESSAGE);
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                messages,
                request
        );
    }

    /**
     * Avoids a hard compile-time dependency on the Turnstile exception type so that
     * the security module can remain isolated.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        if ("io.therapistai.security.turnstile.TurnstileVerificationException"
                .equals(exception.getClass().getName())) {
            return buildResponse(
                    HttpStatus.FORBIDDEN,
                    List.of("Turnstile verification failed."),
                    request
            );
        }

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Unexpected server error"),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Unexpected server error"),
                request
        );
    }

    private static ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            List<String> messages,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        messages,
                        request.getRequestURI()
                ));
    }

    private static List<String> extractValidationMessages(
            MethodArgumentNotValidException exception
    ) {
        List<String> messages = new ArrayList<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            messages.add(formatFieldError(fieldError));
        }

        for (ObjectError globalError : exception.getBindingResult().getGlobalErrors()) {
            messages.add(globalError.getDefaultMessage());
        }

        if (messages.isEmpty()) {
            messages.add("Invalid request.");
        }

        return messages;
    }

    private static String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private static boolean isChatEndpoint(HttpServletRequest request) {
        return request != null
                && request.getRequestURI() != null
                && request.getRequestURI().startsWith("/api/chat");
    }

    @ExceptionHandler(InvalidChatMessageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidChatMessageException(
            InvalidChatMessageException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        List.of(exception.getMessage()),
                        request.getRequestURI()
                ));
    }
}