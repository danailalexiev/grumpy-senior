package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public record Message(
        long id,
        Payload payload
) {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Payload.User.class, name = "user"),
            @JsonSubTypes.Type(value = Payload.Bot.class, name = "bot")
    })
    public sealed interface Payload {

        record User(String content) implements Payload {
        }

        record Bot(String content) implements Payload {
        }
    }

    static Message fromEntity(MessageEntity entity, ObjectMapper objectMapper) {
        Objects.requireNonNull(entity, "entity must not be null");
        Objects.requireNonNull(objectMapper, "objectMapper must not be null");

        final Payload payload = switch (entity.type()) {
            case MessageEntity.Type.USER -> objectMapper.readValue(entity.payload(), Payload.User.class);
            case MessageEntity.Type.BOT -> objectMapper.readValue(entity.payload(), Payload.Bot.class);
        };

        return new Message(entity.id(), payload);
    }

}
