package io.therapistai.security.domain;

/**
 * Thrown when a registration attempt uses an already-registered email address.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email already registered: " + email);
    }
}

