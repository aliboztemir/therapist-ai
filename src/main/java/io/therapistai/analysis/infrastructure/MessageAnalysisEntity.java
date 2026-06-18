package io.therapistai.analysis.infrastructure;

import io.therapistai.analysis.domain.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "message_analysis",
        indexes = {
                @Index(name = "idx_message_analysis_message_id", columnList = "message_id"),
                @Index(name = "idx_message_analysis_user_id", columnList = "user_id"),
                @Index(name = "idx_message_analysis_conversation_id", columnList = "conversation_id")
        }
)
public class MessageAnalysisEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "message_id", nullable = false, unique = true)
    private UUID messageId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_intent", nullable = false)
    private UserIntent userIntent;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_emotion", nullable = false)
    private EmotionType primaryEmotion;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_emotion", nullable = false)
    private EmotionType secondaryEmotion;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment", nullable = false)
    private SentimentType sentiment;

    @Column(name = "emotional_intensity", nullable = false)
    private short emotionalIntensity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "message_analysis_themes",
            joinColumns = @JoinColumn(name = "message_analysis_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "theme")
    private Set<MessageTheme> themes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "temporal_focus", nullable = false)
    private TemporalFocus temporalFocus;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "message_analysis_communication_styles",
            joinColumns = @JoinColumn(name = "message_analysis_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "communication_style")
    private Set<CommunicationStyle> communicationStyles = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "message_analysis_cognitive_signals",
            joinColumns = @JoinColumn(name = "message_analysis_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "cognitive_signal")
    private Set<CognitiveSignal> cognitiveSignals = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "disclosure_level", nullable = false)
    private DisclosureLevel disclosureLevel;

    @Column(name = "advice_seeking", nullable = false)
    private boolean adviceSeeking;

    @Column(name = "boundary_test", nullable = false)
    private boolean boundaryTest;

    @Column(name = "crisis_signal_detected", nullable = false)
    private boolean crisisSignalDetected;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "fallback_reason")
    private String fallbackReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public MessageAnalysisEntity(
            UUID id,
            UUID messageId,
            UUID userId,
            UUID conversationId,
            MessageType messageType,
            UserIntent userIntent,
            EmotionType primaryEmotion,
            EmotionType secondaryEmotion,
            SentimentType sentiment,
            int emotionalIntensity,
            Set<MessageTheme> themes,
            TemporalFocus temporalFocus,
            Set<CommunicationStyle> communicationStyles,
            Set<CognitiveSignal> cognitiveSignals,
            DisclosureLevel disclosureLevel,
            boolean adviceSeeking,
            boolean boundaryTest,
            boolean crisisSignalDetected,
            double confidence,
            String fallbackReason,
            Instant createdAt
    ) {
        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.messageType = messageType;
        this.userIntent = userIntent;
        this.primaryEmotion = primaryEmotion;
        this.secondaryEmotion = secondaryEmotion;
        this.sentiment = sentiment;
        this.emotionalIntensity = (short) emotionalIntensity;
        this.temporalFocus = temporalFocus;
        this.disclosureLevel = disclosureLevel;
        this.adviceSeeking = adviceSeeking;
        this.boundaryTest = boundaryTest;
        this.crisisSignalDetected = crisisSignalDetected;
        this.confidence = confidence;
        this.fallbackReason = fallbackReason;
        this.createdAt = createdAt;

        if (themes != null) {
            this.themes.addAll(themes);
        }

        if (communicationStyles != null) {
            this.communicationStyles.addAll(communicationStyles);
        }

        if (cognitiveSignals != null) {
            this.cognitiveSignals.addAll(cognitiveSignals);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public UserIntent getUserIntent() {
        return userIntent;
    }

    public EmotionType getPrimaryEmotion() {
        return primaryEmotion;
    }

    public EmotionType getSecondaryEmotion() {
        return secondaryEmotion;
    }

    public SentimentType getSentiment() {
        return sentiment;
    }

    public int getEmotionalIntensity() {
        return emotionalIntensity;
    }

    public Set<MessageTheme> getThemes() {
        return themes;
    }

    public TemporalFocus getTemporalFocus() {
        return temporalFocus;
    }

    public Set<CommunicationStyle> getCommunicationStyles() {
        return communicationStyles;
    }

    public Set<CognitiveSignal> getCognitiveSignals() {
        return cognitiveSignals;
    }

    public DisclosureLevel getDisclosureLevel() {
        return disclosureLevel;
    }

    public boolean isAdviceSeeking() {
        return adviceSeeking;
    }

    public boolean isBoundaryTest() {
        return boundaryTest;
    }

    public boolean isCrisisSignalDetected() {
        return crisisSignalDetected;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}