package io.therapistai.auth;

import java.util.UUID;

/**
 * Shared authentication contract.
 *
 * <p>Implemented by the Spring Security principal so application code can obtain
 * the authenticated {@code userId} without additional database lookups.
 */
public interface UserIdentity {

    UUID userId();
}
