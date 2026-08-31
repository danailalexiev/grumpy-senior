package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.persistence.ConversationEntity;

import java.util.Objects;

public record Conversation(
        long id,
        String title
) {

    static Conversation fromEntity(ConversationEntity entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        return new Conversation(entity.id(), entity.title());
    }

}
