package io.therapistai.analysis.application;

import io.therapistai.ai.application.AIProviderGateway;
import io.therapistai.ai.domain.AIRequest;
import io.therapistai.analysis.domain.MessageAnalysis;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DefaultMessageAnalysisService
        implements MessageAnalysisService {

    private final MessageAnalysisPromptBuilder promptBuilder;
    private final MessageAnalysisParser parser;
    private final MessageAnalysisRepository repository;
    private final AIProviderGateway aiGateway;

    public DefaultMessageAnalysisService(
            MessageAnalysisPromptBuilder promptBuilder,
            MessageAnalysisParser parser,
            MessageAnalysisRepository repository,
            AIProviderGateway aiGateway
    ) {
        this.promptBuilder =
                Objects.requireNonNull(promptBuilder);

        this.parser =
                Objects.requireNonNull(parser);

        this.repository =
                Objects.requireNonNull(repository);

        this.aiGateway =
                Objects.requireNonNull(aiGateway);
    }

    @Override
    public MessageAnalysis analyze(
            AnalysisInput input
    ) {
        Objects.requireNonNull(
                input,
                "input must not be null"
        );

        String prompt =
                promptBuilder.build(input).content();

        String rawResponse =
                aiGateway.generate(
                        new AIRequest(prompt)
                ).content();

        MessageAnalysis analysis =
                parser.parse(rawResponse);

        repository.save(
                input.userId(),
                input.conversationId(),
                input.messageId(),
                analysis
        );

        return analysis;
    }
}