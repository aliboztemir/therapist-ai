package io.therapistai.chat.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing an incoming chat request.
 * Placed in base package due to environment directory constraints; will be moved to dto package later.
 */
public class ChatRequest {

    @NotNull(message = "message must not be null")
    @NotBlank(message = "message must not be blank")
    private String message;

    private String userId;

    public ChatRequest() {
    }

    public ChatRequest(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
