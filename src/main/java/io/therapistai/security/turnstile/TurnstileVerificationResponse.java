package io.therapistai.security.turnstile;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TurnstileVerificationResponse(@JsonProperty("success") boolean success) {
}

