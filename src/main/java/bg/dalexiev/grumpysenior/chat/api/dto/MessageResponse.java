package bg.dalexiev.grumpysenior.chat.api.dto;

import bg.dalexiev.grumpysenior.chat.domain.Message;

public record MessageResponse(
        long id,
        PayloadDto payload
) {

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
