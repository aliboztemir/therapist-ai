package io.therapistai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControllerOpenApiKey {

    @GetMapping("/test")
    public String test() {
        return System.getenv("OPENAI_API_KEY") != null ? "KEY_FOUND" : "KEY_MISSING";
    }
}
