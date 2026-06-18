package io.therapistai.security.domain;

/**
 * Thrown when a registration attempt uses an already-taken username.
 */
public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("Username already taken: " + username);
    }
}

