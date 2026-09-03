package bg.dalexiev.grumpysenior.chat.api.dto;

import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PayloadDto.User.CodeSubmission.class, name = "code-submission"),
        @JsonSubTypes.Type(value = PayloadDto.User.Prompt.class, name = "prompt"),
        @JsonSubTypes.Type(value = PayloadDto.Bot.class, name = "bot")
})
public sealed interface PayloadDto {

    sealed interface User extends PayloadDto {

        record CodeSubmission(String message, String code) implements User {}

        record Prompt(String message) implements User {}

        default Message.Payload.User toDomain() {
            return switch (this) {
                case CodeSubmission codeSubmission -> new Message.Payload.User.CodeSubmission(codeSubmission.message, codeSubmission.code);
                case Prompt prompt -> new Message.Payload.User.Prompt(prompt.message);
            };
        }

    }

    record Bot(String content) implements PayloadDto {
    }

}
