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
                    case Message.Payload.User.CodeSubmission codeSubmission -> new PayloadDto.User.CodeSubmission(codeSubmission.message(), codeSubmission.code());
                    case Message.Payload.User.Prompt prompt -> new PayloadDto.User.Prompt(prompt.message());
                    case Message.Payload.Bot botPayload -> new PayloadDto.Bot(botPayload.content());
                }
        );
    }
}
