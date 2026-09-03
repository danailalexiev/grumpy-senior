package bg.dalexiev.grumpysenior.chat.domain.ai;

import bg.dalexiev.grumpysenior.chat.domain.Message;
import org.jspecify.annotations.NonNull;

public interface TitleGenerator {

    String generateTitle(Message.Payload.@NonNull User userPrompt, Message.Payload.@NonNull Bot botAnswer);

}
