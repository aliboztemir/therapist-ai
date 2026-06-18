package io.therapistai.conversation.api;

import io.therapistai.conversation.application.ConversationHistoryService;
import io.therapistai.conversation.application.ConversationQueryService;
import io.therapistai.conversation.domain.ChatMessage;
import io.therapistai.conversation.domain.Conversation;
import io.therapistai.security.application.AppUserDetailsService;
import io.therapistai.security.domain.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationQueryService conversationQueryService;
    private final ConversationHistoryService conversationHistoryService;
    private final AppUserDetailsService appUserDetailsService;

    public ConversationController(
            ConversationQueryService conversationQueryService,
            ConversationHistoryService conversationHistoryService,
            AppUserDetailsService appUserDetailsService
    ) {
        this.conversationQueryService = conversationQueryService;
        this.conversationHistoryService = conversationHistoryService;
        this.appUserDetailsService = appUserDetailsService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> list(
            Authentication authentication
    ) {
        AuthenticatedUser user = resolveUser(authentication);

        List<ConversationSummaryResponse> response =
                conversationQueryService.findUserConversations(user.userId())
                        .stream()
                        .sorted(Comparator.comparing(Conversation::updatedAt).reversed())
                        .map(this::toSummaryResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ConversationMessagesResponse> messages(
            Authentication authentication,
            @PathVariable UUID conversationId
    ) {
        AuthenticatedUser user = resolveUser(authentication);

        List<ConversationMessageResponse> messages =
                conversationHistoryService.getConversationHistory(
                                user.userId(),
                                conversationId
                        )
                        .stream()
                        .map(this::toMessageResponse)
                        .toList();

        return ResponseEntity.ok(
                new ConversationMessagesResponse(
                        conversationId.toString(),
                        messages
                )
        );
    }

    private AuthenticatedUser resolveUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails principal)) {
            throw new IllegalStateException("Authenticated principal is invalid.");
        }

        return appUserDetailsService.resolveAuthenticatedUser(principal);
    }

    private ConversationSummaryResponse toSummaryResponse(Conversation conversation) {
        return new ConversationSummaryResponse(
                conversation.id().toString(),
                conversation.createdAt(),
                conversation.updatedAt()
        );
    }

    private ConversationMessageResponse toMessageResponse(ChatMessage message) {
        return new ConversationMessageResponse(
                message.id().toString(),
                message.role().name(),
                message.content(),
                message.messageOrder(),
                message.createdAt()
        );
    }
}