package io.therapistai.security.turnstile;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TurnstileVerificationService {

    private static final Logger log = LoggerFactory.getLogger(TurnstileVerificationService.class);
    private static final String VERIFY_URL =
            "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final RestClient restClient;
    private final String secretKey;

    public TurnstileVerificationService(
            RestClient.Builder restClientBuilder,
            @Value("${turnstile.secret-key:}") String secretKey) {
        this.restClient = restClientBuilder.build();
        this.secretKey = secretKey;
    }

    @PostConstruct
    void logStartupMode() {
        if (secretKey.isBlank()) {
            log.warn("Turnstile secret key (TURNSTILE_SECRET_KEY) is not configured. " +
                     "Turnstile verification is DISABLED — all requests will be allowed. " +
                     "Do NOT run this way in production.");
        } else {
            log.info("Turnstile verification is ENABLED.");
        }
    }

    /**
     * Verifies a Cloudflare Turnstile token.
     *
     * <p>If {@code TURNSTILE_SECRET_KEY} is not configured (blank), verification is skipped
     * and the method returns {@code true} — local development mode.
     *
     * <p>If the secret key is configured:
     * <ul>
     *   <li>A null or blank token returns {@code false} immediately.</li>
     *   <li>A non-null token is verified against the Cloudflare siteverify API.</li>
     * </ul>
     */
    public boolean verify(String token) {
        if (secretKey.isBlank()) {
            return true;
        }
        if (token == null || token.isBlank()) {
            log.warn("Turnstile token is missing");
            return false;
        }
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("secret", secretKey);
            form.add("response", token);

            TurnstileVerificationResponse response = restClient.post()
                    .uri(VERIFY_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TurnstileVerificationResponse.class);

            boolean success = response != null && response.success();
            if (!success) {
                log.warn("Turnstile verification returned success=false for token");
            }
            return success;
        } catch (Exception e) {
            log.warn("Turnstile verification call failed: {}", e.getMessage());
            return false;
        }
    }
}

