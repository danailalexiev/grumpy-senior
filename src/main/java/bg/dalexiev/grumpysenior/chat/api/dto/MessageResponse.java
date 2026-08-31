package bg.dalexiev.grumpysenior.chat.api.dto;

import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record MessageResponse(
        long id,
        PayloadDto payload
) {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PayloadDto.User.class, name = "user"),
            @JsonSubTypes.Type(value = PayloadDto.Bot.class, name = "bot")
    })
    sealed interface PayloadDto {

        record User(String content) implements PayloadDto {
        }

        record Bot(String content) implements PayloadDto {
        }
    }

    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.id(),
                switch (message.payload()) {
                    case Message.Payload.User userPayload -> new PayloadDto.User(userPayload.content());
                    case Message.Payload.Bot botPayload -> new PayloadDto.Bot(botPayload.content());
                }
        );
    }
}
