package io.therapistai.security.turnstile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TurnstileVerificationServiceTest {

    private static final String VERIFY_URL =
            "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Mock private RestClient.Builder mockRestClientBuilder;
    @Mock private RestClient mockRestClient;

    // ── Dev mode (no secret configured) ─────────────────────────────────────

    @Test
    void verify_noSecretConfigured_anyToken_shouldPass() {
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);
        TurnstileVerificationService service =
                new TurnstileVerificationService(mockRestClientBuilder, "");

        assertTrue(service.verify("any-token"),
                "Dev mode: blank secret key must always pass regardless of token");
    }

    @Test
    void verify_noSecretConfigured_nullToken_shouldPass() {
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);
        TurnstileVerificationService service =
                new TurnstileVerificationService(mockRestClientBuilder, "");

        assertTrue(service.verify(null),
                "Dev mode: blank secret key must always pass even with null token");
    }

    // ── Prod mode — missing / blank token ───────────────────────────────────

    @Test
    void verify_secretConfigured_nullToken_shouldFail() {
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);
        TurnstileVerificationService service =
                new TurnstileVerificationService(mockRestClientBuilder, "real-secret");

        assertFalse(service.verify(null), "Prod mode: null token must fail");
    }

    @Test
    void verify_secretConfigured_blankToken_shouldFail() {
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);
        TurnstileVerificationService service =
                new TurnstileVerificationService(mockRestClientBuilder, "real-secret");

        assertFalse(service.verify("   "), "Prod mode: blank token must fail");
    }

    // ── Prod mode — Cloudflare API responses (MockRestServiceServer) ─────────

    @Test
    void verify_secretConfigured_validToken_shouldPass() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
        mockServer.expect(requestTo(VERIFY_URL))
                  .andExpect(method(HttpMethod.POST))
                  .andRespond(withSuccess("{\"success\":true}", MediaType.APPLICATION_JSON));

        TurnstileVerificationService service =
                new TurnstileVerificationService(builder, "real-secret");

        assertTrue(service.verify("valid-token"), "Valid Cloudflare response must pass");
        mockServer.verify();
    }

    @Test
    void verify_secretConfigured_invalidToken_shouldFail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
        mockServer.expect(requestTo(VERIFY_URL))
                  .andExpect(method(HttpMethod.POST))
                  .andRespond(withSuccess("{\"success\":false}", MediaType.APPLICATION_JSON));

        TurnstileVerificationService service =
                new TurnstileVerificationService(builder, "real-secret");

        assertFalse(service.verify("bad-token"), "Invalid Cloudflare response must fail");
        mockServer.verify();
    }

    @Test
    void verify_secretConfigured_cloudflareThrows_shouldFail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
        mockServer.expect(requestTo(VERIFY_URL))
                  .andExpect(method(HttpMethod.POST))
                  .andRespond(withServerError());

        TurnstileVerificationService service =
                new TurnstileVerificationService(builder, "real-secret");

        assertFalse(service.verify("some-token"),
                "Server error from Cloudflare must be treated as failed verification");
    }
}
