package io.therapistai.conversation.application;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class ChatMessageValidator {

    private static final Set<String> INVALID_LITERAL_MESSAGES = Set.of(
            "null",
            "\"\"",
            "''",
            "undefined",
            "nan"
    );

    private static final int MIN_MEANINGFUL_LENGTH = 2;

    public boolean isMeaningful(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }

        String normalized = normalize(message);

        if (INVALID_LITERAL_MESSAGES.contains(normalized)) {
            return false;
        }

        if (normalized.length() < MIN_MEANINGFUL_LENGTH) {
            return false;
        }

        return containsLetterOrDigit(message);
    }

    public void validate(String message) {
        if (!isMeaningful(message)) {
            throw new InvalidChatMessageException(
                    "Mesajınızı anlayamadım. Daha açık yazar mısınız?"
            );
        }
    }

    private static String normalize(String value) {
        return value
                .strip()
                .toLowerCase(Locale.ROOT);
    }

    private static boolean containsLetterOrDigit(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetterOrDigit(value.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}