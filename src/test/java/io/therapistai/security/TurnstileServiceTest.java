package io.therapistai.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnstileServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Test
    void verify_shouldReturnTrue_whenSecretKeyIsBlank_devMode() {
        when(restClientBuilder.build()).thenReturn(restClient);
        TurnstileService service = new TurnstileService(restClientBuilder, "");

        assertTrue(service.verify("any-token"),
                "Dev mode: blank secret key must always pass regardless of token");
    }

    @Test
    void verify_shouldReturnTrue_whenSecretKeyIsBlank_andTokenIsNull() {
        when(restClientBuilder.build()).thenReturn(restClient);
        TurnstileService service = new TurnstileService(restClientBuilder, "");

        assertTrue(service.verify(null),
                "Dev mode: blank secret key must always pass even with null token");
    }

    @Test
    void verify_shouldReturnFalse_whenSecretKeyIsSet_andTokenIsNull() {
        when(restClientBuilder.build()).thenReturn(restClient);
        TurnstileService service = new TurnstileService(restClientBuilder, "real-secret-key");

        assertFalse(service.verify(null), "Prod mode: null token must fail");
    }

    @Test
    void verify_shouldReturnFalse_whenSecretKeyIsSet_andTokenIsBlank() {
        when(restClientBuilder.build()).thenReturn(restClient);
        TurnstileService service = new TurnstileService(restClientBuilder, "real-secret-key");

        assertFalse(service.verify("   "), "Prod mode: blank token must fail");
    }
}

