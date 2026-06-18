package io.therapistai.security.api;

import io.therapistai.security.application.AppUserDetailsService;
import io.therapistai.security.application.ConsentService;
import io.therapistai.security.domain.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for the legal consent / onboarding flow.
 *
 * <p>Uses the same principal-resolution pattern as ChatController:
 * {@code @AuthenticationPrincipal UserDetails} + {@code AppUserDetailsService.resolveAuthenticatedUser()}.
 * Injecting {@code AuthenticatedUser} directly would return {@code null} because Spring Security
 * stores a Spring {@code UserDetails} object as the principal, not {@code AuthenticatedUser}.
 */
@RestController
@RequestMapping("/api/consent")
public class ConsentController {

    private final ConsentService consentService;
    private final AppUserDetailsService appUserDetailsService;

    public ConsentController(ConsentService consentService,
                             AppUserDetailsService appUserDetailsService) {
        this.consentService = consentService;
        this.appUserDetailsService = appUserDetailsService;
    }

    /**
     * Returns whether the authenticated user has already accepted the consent.
     * GET /api/consent/status → { "consentGiven": true|false }
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus(
            @AuthenticationPrincipal UserDetails principal) {
        AuthenticatedUser user = appUserDetailsService.resolveAuthenticatedUser(principal);
        boolean accepted = consentService.isConsentGiven(user.userId());
        return ResponseEntity.ok(Map.of("consentGiven", accepted));
    }

    /**
     * Records that the authenticated user accepts the legal consent.
     * POST /api/consent/accept → { "consentGiven": true }
     */
    @PostMapping("/accept")
    public ResponseEntity<Map<String, Boolean>> acceptConsent(
            @AuthenticationPrincipal UserDetails principal) {
        AuthenticatedUser user = appUserDetailsService.resolveAuthenticatedUser(principal);
        consentService.acceptConsent(user.userId());
        return ResponseEntity.ok(Map.of("consentGiven", true));
    }
}

