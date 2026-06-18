package io.therapistai.security.application;

import java.util.UUID;

/**
 * Public API for consent / onboarding management.
 */
public interface ConsentService {

    /**
     * Returns true if the user has already accepted the legal consent.
     */
    boolean isConsentGiven(UUID userId);

    /**
     * Records that the user has accepted the legal consent.
     */
    void acceptConsent(UUID userId);
}

