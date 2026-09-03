package bg.dalexiev.grumpysenior.agent.codereview.agui;

import com.agui.core.message.*;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.util.List;

public class AgUiMessageMapper {

    private AgUiMessageMapper() {
    }

    public static AgUiMessageMapper newInstance() {
        return new AgUiMessageMapper();
    }

    public @Nullable Message mapToSpringAiMessage(BaseMessage message) {
        return switch (message) {
            case UserMessage userMessage ->
                    org.springframework.ai.chat.messages.UserMessage.builder().text(userMessage.getContent()).build();

            case AssistantMessage assistantMessage ->
                    org.springframework.ai.chat.messages.AssistantMessage.builder()
                            .content(assistantMessage.getContent())
                            .toolCalls(
                                    assistantMessage.getToolCalls().stream()
                                            .map(toolCall -> new org.springframework.ai.chat.messages.AssistantMessage.ToolCall(
                                                            toolCall.id(),
                                                            toolCall.type(),
                                                            toolCall.function().name(),
                                                            toolCall.function().arguments()
                                                    )
                                            ).toList()
                            )
                            .build();
            case SystemMessage systemMessage -> org.springframework.ai.chat.messages.SystemMessage.builder()
                    .text(systemMessage.getContent())
                    .build();

            case ToolMessage toolMessage -> org.springframework.ai.chat.messages.ToolResponseMessage.builder()
                    .responses(
                            List.of(
                                    new ToolResponseMessage.ToolResponse(toolMessage.getToolCallId(), toolMessage.getName(), toolMessage.getContent())
                            )
                    )
                    .build();
            default -> null;
        };
    }

}
