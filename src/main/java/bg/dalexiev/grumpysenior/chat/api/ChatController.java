package bg.dalexiev.grumpysenior.chat.api;

import bg.dalexiev.grumpysenior.chat.api.dto.ConversationResponse;
import bg.dalexiev.grumpysenior.chat.api.dto.MessageResponse;
import bg.dalexiev.grumpysenior.chat.domain.ChatService;
import bg.dalexiev.grumpysenior.chat.domain.Conversation;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import bg.dalexiev.grumpysenior.util.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> listConversations(@AuthenticationPrincipal Jwt jwt) {
        final List<ConversationResponse> conversations = chatService.getAllConversationsByUser(getUserId(jwt)).stream()
                .map(ConversationResponse::from)
                .toList();
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(@AuthenticationPrincipal Jwt jwt) {
        final Conversation conversation = chatService.create(getUserId(jwt));
        return ResponseEntity.created(URI.create("/conversation/" + conversation.id()))
                .body(ConversationResponse.from(conversation));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> listMessagesForConversation(@PathVariable Long conversationId, @AuthenticationPrincipal Jwt jwt) {
        return switch (chatService.listMessagesForConversation(getUserId(jwt), conversationId)) {
            case Either.Left<ChatService.Error, List<Message>> error -> onError(error);
            case Either.Right<ChatService.Error, List<Message>> success ->
                    ResponseEntity.ok(success.value().stream().map(MessageResponse::from).toList());
        };
    }

    private static <R> ResponseEntity<R> onError(Either.Left<ChatService.Error, ?> error) {
        return switch (error.value()) {
            case ChatService.Error.InvalidConversation ignored -> ResponseEntity.notFound().build();
            case ChatService.Error.InvalidOwner ignored -> ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        };
    }

    private static long getUserId(Jwt jwt) {
        return Long.parseLong(Objects.requireNonNull(jwt.getClaimAsString("userId")));
    }
}
