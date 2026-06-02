package io.therapistai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes public application configuration flags to the frontend.
 * No sensitive values are returned.
 */
@RestController
@RequestMapping("/api/config")
public class AppConfigController {

    private final boolean turnstileEnabled;

    public AppConfigController(
            @Value("${turnstile.secret-key:}") String secretKey) {
        this.turnstileEnabled = !secretKey.isBlank();
    }

    @GetMapping
    public ResponseEntity<AppConfigResponse> getConfig() {
        return ResponseEntity.ok(new AppConfigResponse(turnstileEnabled));
    }

    public record AppConfigResponse(boolean turnstileEnabled) {}
}

