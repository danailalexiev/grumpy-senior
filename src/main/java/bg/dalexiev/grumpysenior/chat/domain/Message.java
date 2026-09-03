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
            @JsonSubTypes.Type(value = Payload.User.CodeSubmission.class, name = "code-submission"),
            @JsonSubTypes.Type(value = Payload.User.Prompt.class, name = "prompt"),
            @JsonSubTypes.Type(value = Payload.Bot.class, name = "bot")
    })
    public sealed interface Payload {

        sealed interface User extends Payload {

            record CodeSubmission(String message, String code) implements User {
            }

            record Prompt(String message) implements User {
            }
        }

        record Bot(String content) implements Payload {
        }
    }

    static Message fromEntity(MessageEntity entity, JsonMapper jsonMapper) {
        Objects.requireNonNull(entity, "entity must not be null");
        Objects.requireNonNull(jsonMapper, "jsonMapper must not be null");

        final Payload payload = switch (entity.type()) {
            case MessageEntity.Type.CODE_SUBMISSION -> jsonMapper.readValue(entity.payload().value(), Payload.User.CodeSubmission.class);
            case MessageEntity.Type.PROMPT -> jsonMapper.readValue(entity.payload().value(), Payload.User.Prompt.class);
            case MessageEntity.Type.BOT -> jsonMapper.readValue(entity.payload().value(), Payload.Bot.class);
        };

        return new Message(entity.id(), payload);
    }

}
