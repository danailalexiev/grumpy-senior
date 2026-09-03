package bg.dalexiev.grumpysenior.agent.mapper;

import bg.dalexiev.grumpysenior.chat.domain.Message;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class MessagePayloadMapper {

    private MessagePayloadMapper() {
    }

    public static MessagePayloadMapper newInstance() {
        return new MessagePayloadMapper();
    }

    public String toAiReadableContent(Message.@NonNull Payload payload) {
        return switch (Objects.requireNonNull(payload, "payload must not be null")) {
            case Message.Payload.User.CodeSubmission codeSubmission -> codeSubmission.message() + "\nSubmitted code:\n" + codeSubmission.code();
            case Message.Payload.User.Prompt prompt -> prompt.message();
            case Message.Payload.Bot botPayload -> botPayload.content();
        };
    }

}
