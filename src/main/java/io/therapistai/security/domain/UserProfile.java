package io.therapistai.security.domain;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Lightweight read-only view of the user's profile for the chat pipeline.
 */
public record UserProfile(
        UUID userId,
        String username,
        String email,
        String fullName,
        String preferredName,
        LocalDate birthDate,
        String gender,
        String country,
        String city,
        String preferredLanguage,
        String timezone,
        boolean onboardingCompleted
) {
}



