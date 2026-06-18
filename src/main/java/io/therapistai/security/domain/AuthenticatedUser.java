package io.therapistai.security.domain;

import java.util.UUID;

/**
 * Immutable value object representing an authenticated user resolved
 * from Spring Security's Authentication context.
 *
 * <p>Created by
 * {@link io.therapistai.security.application.AppUserDetailsService#resolveAuthenticatedUser}
 * at the start of every authenticated request so that all downstream
 * services work with a typed identity instead of raw principal strings.
 */
public record AuthenticatedUser(
        UUID userId,
        String username,
        String email,
        String fullName
) {
}

