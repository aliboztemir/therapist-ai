package io.therapistai.security.turnstile;

public class TurnstileVerificationException extends RuntimeException {

    public TurnstileVerificationException(String message) {
        super(message);
    }
}

