package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.json.JsonMapper;

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

    static Message fromEntity(MessageEntity entity, JsonMapper jsonMapper) {
        Objects.requireNonNull(entity, "entity must not be null");
        Objects.requireNonNull(jsonMapper, "jsonMapper must not be null");

        final Payload payload = switch (entity.type()) {
            case MessageEntity.Type.USER -> jsonMapper.readValue(entity.payload().value(), Payload.User.class);
            case MessageEntity.Type.BOT -> jsonMapper.readValue(entity.payload().value(), Payload.Bot.class);
        };

        return new Message(entity.id(), payload);
    }

}
