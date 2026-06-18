package io.therapistai.ai.application;

import io.therapistai.ai.domain.AIRequest;
import io.therapistai.ai.domain.AIResponse;

public interface AIProviderGateway {

    AIResponse generate(AIRequest request);
}