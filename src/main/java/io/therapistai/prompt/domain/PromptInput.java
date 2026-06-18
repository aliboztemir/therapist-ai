package io.therapistai.prompt.domain;

import io.therapistai.memory.domain.MemorySnapshot;
import io.therapistai.risk.domain.RiskDecision;

/**
 * All data needed to assemble the final prompt.
 *
 * <p>Field order mirrors the consumption order in {@code PromptAssemblyService}:
 * system-prompt fields first (riskDecision, therapyDecision, session context),
 * then the user-prompt field (currentMessage — raw user text only).
 *
 * <p>Conversation history is passed separately via {@code AIRequest.conversationHistory}
 * and is never embedded into the prompt text.
 */
public record PromptInput(

        // ── system prompt inputs ──────────────────────────────────────────────
        RiskDecision riskDecision,

        // ── session context (injected into system prompt) ─────────────────────
        String userFullName,

        MemorySnapshot memorySnapshot,

        // ── user prompt input ─────────────────────────────────────────────────
        String currentMessage

) {
}