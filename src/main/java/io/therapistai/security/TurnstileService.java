package io.therapistai.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TurnstileService {

    private static final Logger log = LoggerFactory.getLogger(TurnstileService.class);
    private static final String VERIFY_URL =
            "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final RestClient restClient;
    private final String secretKey;

    public TurnstileService(
            RestClient.Builder restClientBuilder,
            @Value("${turnstile.secret-key:}") String secretKey) {
        this.restClient = restClientBuilder.build();
        this.secretKey = secretKey;
    }

    /**
     * Verifies a Cloudflare Turnstile token.
     * If {@code turnstile.secret-key} is blank (dev/local), verification is skipped → always true.
     */
    public boolean verify(String token) {
        if (secretKey.isBlank()) {
            log.debug("Turnstile secret key not configured — skipping verification (dev mode)");
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

            TurnstileVerifyResponse response = restClient.post()
                    .uri(VERIFY_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TurnstileVerifyResponse.class);

            return response != null && response.success();
        } catch (Exception e) {
            log.warn("Turnstile verification call failed: {}", e.getMessage());
            return false;
        }
    }

    private record TurnstileVerifyResponse(@JsonProperty("success") boolean success) {
    }
}

