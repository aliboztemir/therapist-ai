package io.therapistai.analysis.domain;

import java.util.Collections;
import java.util.List;

public final class MessageAnalysis {

    private final MessageType messageType;
    private final UserIntent userIntent;
    private final EmotionType primaryEmotion;
    private final EmotionType secondaryEmotion;
    private final SentimentType sentiment;
    private final int emotionalIntensity;
    private final List<MessageTheme> themes;
    private final TemporalFocus temporalFocus;
    private final List<CommunicationStyle> communicationStyles;
    private final List<CognitiveSignal> cognitiveSignals;
    private final DisclosureLevel disclosureLevel;
    private final boolean adviceSeeking;
    private final boolean boundaryTest;
    private final boolean crisisSignalDetected;
    private final double confidence;
    private final String fallbackReason;

    private MessageAnalysis(Builder builder) {
        this.messageType = builder.messageType;
        this.userIntent = builder.userIntent;
        this.primaryEmotion = builder.primaryEmotion;
        this.secondaryEmotion = builder.secondaryEmotion;
        this.sentiment = builder.sentiment;
        this.emotionalIntensity = builder.emotionalIntensity;
        this.themes = List.copyOf(builder.themes);
        this.temporalFocus = builder.temporalFocus;
        this.communicationStyles = List.copyOf(builder.communicationStyles);
        this.cognitiveSignals = List.copyOf(builder.cognitiveSignals);
        this.disclosureLevel = builder.disclosureLevel;
        this.adviceSeeking = builder.adviceSeeking;
        this.boundaryTest = builder.boundaryTest;
        this.crisisSignalDetected = builder.crisisSignalDetected;
        this.confidence = builder.confidence;
        this.fallbackReason = builder.fallbackReason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MessageAnalysis safeDefault(String reason) {
        return builder()
                .messageType(MessageType.UNKNOWN)
                .userIntent(UserIntent.UNKNOWN)
                .primaryEmotion(EmotionType.UNKNOWN)
                .secondaryEmotion(EmotionType.UNKNOWN)
                .sentiment(SentimentType.UNKNOWN)
                .emotionalIntensity(0)
                .themes(Collections.emptyList())
                .temporalFocus(TemporalFocus.UNKNOWN)
                .communicationStyles(Collections.emptyList())
                .cognitiveSignals(Collections.emptyList())
                .disclosureLevel(DisclosureLevel.MINIMAL)
                .adviceSeeking(false)
                .boundaryTest(false)
                .crisisSignalDetected(false)
                .confidence(0.0)
                .fallbackReason(reason)
                .build();
    }

    public MessageType messageType() {
        return messageType;
    }

    public UserIntent userIntent() {
        return userIntent;
    }

    public EmotionType primaryEmotion() {
        return primaryEmotion;
    }

    public EmotionType secondaryEmotion() {
        return secondaryEmotion;
    }

    public SentimentType sentiment() {
        return sentiment;
    }

    public int emotionalIntensity() {
        return emotionalIntensity;
    }

    public List<MessageTheme> themes() {
        return themes;
    }

    public TemporalFocus temporalFocus() {
        return temporalFocus;
    }

    public List<CommunicationStyle> communicationStyles() {
        return communicationStyles;
    }

    public List<CognitiveSignal> cognitiveSignals() {
        return cognitiveSignals;
    }

    public DisclosureLevel disclosureLevel() {
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

    public double confidence() {
        return confidence;
    }

    public String fallbackReason() {
        return fallbackReason;
    }

    @Override
    public String toString() {
        return "MessageAnalysis{" +
                "messageType=" + messageType +
                ", userIntent=" + userIntent +
                ", primaryEmotion=" + primaryEmotion +
                ", secondaryEmotion=" + secondaryEmotion +
                ", sentiment=" + sentiment +
                ", emotionalIntensity=" + emotionalIntensity +
                ", themes=" + themes +
                ", temporalFocus=" + temporalFocus +
                ", communicationStyles=" + communicationStyles +
                ", cognitiveSignals=" + cognitiveSignals +
                ", disclosureLevel=" + disclosureLevel +
                ", adviceSeeking=" + adviceSeeking +
                ", boundaryTest=" + boundaryTest +
                ", crisisSignalDetected=" + crisisSignalDetected +
                ", confidence=" + confidence +
                ", fallbackReason='" + fallbackReason + '\'' +
                '}';
    }

    public static final class Builder {

        private MessageType messageType = MessageType.UNKNOWN;
        private UserIntent userIntent = UserIntent.UNKNOWN;
        private EmotionType primaryEmotion = EmotionType.UNKNOWN;
        private EmotionType secondaryEmotion = EmotionType.UNKNOWN;
        private SentimentType sentiment = SentimentType.UNKNOWN;
        private int emotionalIntensity = 0;
        private List<MessageTheme> themes = Collections.emptyList();
        private TemporalFocus temporalFocus = TemporalFocus.UNKNOWN;
        private List<CommunicationStyle> communicationStyles = Collections.emptyList();
        private List<CognitiveSignal> cognitiveSignals = Collections.emptyList();
        private DisclosureLevel disclosureLevel = DisclosureLevel.MINIMAL;
        private boolean adviceSeeking;
        private boolean boundaryTest;
        private boolean crisisSignalDetected;
        private double confidence;
        private String fallbackReason;

        private Builder() {
        }

        public Builder messageType(MessageType value) {
            this.messageType = value != null ? value : MessageType.UNKNOWN;
            return this;
        }

        public Builder userIntent(UserIntent value) {
            this.userIntent = value != null ? value : UserIntent.UNKNOWN;
            return this;
        }

        public Builder primaryEmotion(EmotionType value) {
            this.primaryEmotion = value != null ? value : EmotionType.UNKNOWN;
            return this;
        }

        public Builder secondaryEmotion(EmotionType value) {
            this.secondaryEmotion = value != null ? value : EmotionType.UNKNOWN;
            return this;
        }

        public Builder sentiment(SentimentType value) {
            this.sentiment = value != null ? value : SentimentType.UNKNOWN;
            return this;
        }

        public Builder emotionalIntensity(int value) {
            this.emotionalIntensity = Math.clamp(value, 0, 10);
            return this;
        }

        public Builder themes(List<MessageTheme> value) {
            this.themes = value != null ? value : Collections.emptyList();
            return this;
        }

        public Builder temporalFocus(TemporalFocus value) {
            this.temporalFocus = value != null ? value : TemporalFocus.UNKNOWN;
            return this;
        }

        public Builder communicationStyles(List<CommunicationStyle> value) {
            this.communicationStyles = value != null ? value : Collections.emptyList();
            return this;
        }

        public Builder cognitiveSignals(List<CognitiveSignal> value) {
            this.cognitiveSignals = value != null ? value : Collections.emptyList();
            return this;
        }

        public Builder disclosureLevel(DisclosureLevel value) {
            this.disclosureLevel = value != null ? value : DisclosureLevel.MINIMAL;
            return this;
        }

        public Builder adviceSeeking(boolean value) {
            this.adviceSeeking = value;
            return this;
        }

        public Builder boundaryTest(boolean value) {
            this.boundaryTest = value;
            return this;
        }

        public Builder crisisSignalDetected(boolean value) {
            this.crisisSignalDetected = value;
            return this;
        }

        public Builder confidence(double value) {
            this.confidence = Math.clamp(value, 0.0, 1.0);
            return this;
        }

        public Builder fallbackReason(String value) {
            this.fallbackReason = value;
            return this;
        }

        public MessageAnalysis build() {
            return new MessageAnalysis(this);
        }
    }
}