package io.therapistai.chat.domain;

public record ChatMessage(Role role, String content) {

    public enum Role {
        USER, ASSISTANT
    }
}

