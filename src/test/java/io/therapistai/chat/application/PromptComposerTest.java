package io.therapistai.chat.application;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PromptComposerTest {

    private static Resource resource(String content) {
        return new ByteArrayResource(content.getBytes());
    }

    private PromptComposer composerWith(String identity, String behavior,
                                        String boundaries, String safety) throws IOException {
        return new PromptComposer(
                resource(identity),
                resource(behavior),
                resource(boundaries),
                resource(safety)
        );
    }

    @Test
    void systemPrompt_shouldContainIdentitySection() throws IOException {
        PromptComposer composer = composerWith(
                "You are TherapistAI.",
                "Use reflective listening.",
                "Do not diagnose.",
                "For crisis content, encourage emergency contact."
        );

        assertTrue(composer.systemPrompt().contains("TherapistAI"),
                "Composed prompt must contain identity content");
    }

    @Test
    void systemPrompt_shouldContainBehaviorSection() throws IOException {
        PromptComposer composer = composerWith(
                "You are TherapistAI.",
                "Use reflective listening.",
                "Do not diagnose.",
                "For crisis content, encourage emergency contact."
        );

        assertTrue(composer.systemPrompt().contains("reflective listening"),
                "Composed prompt must contain behavior content");
    }

    @Test
    void systemPrompt_shouldContainBoundariesSection() throws IOException {
        PromptComposer composer = composerWith(
                "You are TherapistAI.",
                "Use reflective listening.",
                "Do not diagnose.",
                "For crisis content, encourage emergency contact."
        );

        assertTrue(composer.systemPrompt().contains("Do not diagnose"),
                "Composed prompt must contain boundaries content");
    }

    @Test
    void systemPrompt_shouldContainSafetySection() throws IOException {
        PromptComposer composer = composerWith(
                "You are TherapistAI.",
                "Use reflective listening.",
                "Do not diagnose.",
                "For crisis content, encourage emergency contact."
        );

        assertTrue(composer.systemPrompt().contains("crisis content"),
                "Composed prompt must contain safety content");
    }

    @Test
    void systemPrompt_shouldReturnSameInstanceOnEveryCall() throws IOException {
        PromptComposer composer = composerWith(
                "You are TherapistAI.",
                "Use reflective listening.",
                "Do not diagnose.",
                "For crisis content, encourage emergency contact."
        );

        String first  = composer.systemPrompt();
        String second = composer.systemPrompt();

        assertSame(first, second,
                "systemPrompt() must return the same String instance — no recomposition per call");
    }
}

