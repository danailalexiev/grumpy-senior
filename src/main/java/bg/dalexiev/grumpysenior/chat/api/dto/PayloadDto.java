package bg.dalexiev.grumpysenior.chat.api.dto;

import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PayloadDto.User.class, name = "user"),
        @JsonSubTypes.Type(value = PayloadDto.Bot.class, name = "bot")
})
public sealed interface PayloadDto {

    record User(String content) implements PayloadDto {

        public Message.Payload.User toDomain() {
            return new Message.Payload.User(content);
        }

    }

    record Bot(String content) implements PayloadDto {
    }

}
