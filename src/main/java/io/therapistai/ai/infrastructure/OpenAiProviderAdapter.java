package io.therapistai.ai.infrastructure;

import io.therapistai.ai.application.AIProviderGateway;
import io.therapistai.ai.domain.AIProviderException;
import io.therapistai.ai.domain.AIRequest;
import io.therapistai.ai.domain.AIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OpenAiProviderAdapter implements AIProviderGateway {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiProviderAdapter.class);

    private final ChatClient chatClient;

    public OpenAiProviderAdapter(
            ChatClient.Builder chatClientBuilder
    ) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AIResponse generate(
            AIRequest request
    ) {
        try {

            log.debug(
                    """
                            ================= AI REQUEST =================
                            {}
                            ==============================================
                            """,
                    request.prompt()
            );

            ChatResponse response = chatClient
                    .prompt()
                    .user(request.prompt())
                    .call()
                    .chatResponse();

            AIResponse aiResponse =
                    toAIResponse(response);

            log.debug(
                    """
                            ================= AI RESPONSE =================
                            {}
                            ===============================================
                            """,
                    aiResponse.content()
            );

            return aiResponse;

        } catch (AIProviderException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AIProviderException(
                    "AI provider call failed.",
                    ex
            );
        }
    }

    private static AIResponse toAIResponse(
            ChatResponse response
    ) {
        String content =
                extractContent(response);

        String model =
                extractModel(response);

        return new AIResponse(
                content,
                model,
                Instant.now()
        );
    }

    private static String extractContent(
            ChatResponse response
    ) {
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null
                || response.getResult().getOutput().getText() == null
                || response.getResult().getOutput().getText().isBlank()) {

            throw new AIProviderException(
                    "AI provider returned empty response."
            );
        }

        return response.getResult()
                .getOutput()
                .getText();
    }

    private static String extractModel(
            ChatResponse response
    ) {
        if (response == null
                || response.getMetadata() == null) {
            return null;
        }

        return response.getMetadata()
                .getModel();
    }
}