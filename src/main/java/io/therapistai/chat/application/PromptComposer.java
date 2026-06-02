package io.therapistai.chat.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Composes the TherapistAI system prompt from modular prompt files at startup.
 *
 * <p>Files are loaded once during bean construction and cached.
 * The composed prompt is reused on every chat request — no disk I/O per request.
 *
 * <p>Sections: identity → behavior → boundaries → safety.
 */
@Component
public class PromptComposer {

    private static final Logger log = LoggerFactory.getLogger(PromptComposer.class);

    private final String composedPrompt;

    public PromptComposer(
            @Value("classpath:prompts/identity.txt")   Resource identity,
            @Value("classpath:prompts/behavior.txt")   Resource behavior,
            @Value("classpath:prompts/boundaries.txt") Resource boundaries,
            @Value("classpath:prompts/safety.txt")     Resource safety
    ) throws IOException {
        this.composedPrompt = compose(identity, behavior, boundaries, safety);
        log.info("System prompt composed from {} sections.", 4);
    }

    /**
     * Returns the fully composed system prompt.
     * This value is computed once at startup and never recomputed.
     */
    public String systemPrompt() {
        return composedPrompt;
    }

    private static String compose(Resource... sections) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Resource section : sections) {
            String content = section.getContentAsString(StandardCharsets.UTF_8).strip();
            if (!content.isBlank()) {
                sb.append(content).append("\n\n");
            }
        }
        return sb.toString().strip();
    }
}

