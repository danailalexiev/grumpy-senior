package bg.dalexiev.grumpysenior.chat.domain.event;

public record FirstTurnCompletedEvent(
        long conversationId,
        String userPrompt,
        String botAnswer
) {
}
