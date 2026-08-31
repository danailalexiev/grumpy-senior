package bg.dalexiev.grumpysenior.chat.api.dto;

import bg.dalexiev.grumpysenior.chat.domain.Conversation;

public record ConversationResponse(
        Long id,
        String name
) {

    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(conversation.id(), conversation.title());
    }

}
