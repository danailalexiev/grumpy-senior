package bg.dalexiev.grumpysenior.chat.domain.event;

import bg.dalexiev.grumpysenior.chat.domain.Message;

public record FirstTurnCompletedEvent(
        long conversationId,
        Message.Payload.User userPrompt,
        Message.Payload.Bot botAnswer
) {
}
