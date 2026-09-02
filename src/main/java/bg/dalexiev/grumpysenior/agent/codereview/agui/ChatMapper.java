package bg.dalexiev.grumpysenior.agent.codereview.agui;

import bg.dalexiev.grumpysenior.chat.domain.ai.AIGateway;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.agui.core.agent.RunAgentParameters;
import com.agui.core.message.AssistantMessage;
import com.agui.core.message.UserMessage;
import com.agui.core.state.State;
import org.jspecify.annotations.NonNull;

public class ChatMapper {

    private ChatMapper() {
    }

    public static ChatMapper newInstance() {
        return new ChatMapper();
    }

    public @NonNull RunAgentParameters mapToRunAgentParameters(AIGateway.Input input) {
        return RunAgentParameters.builder()
                .threadId((String) input.params().get("conversationId"))
                .runId((String) input.params().get("messageId"))
                .state(new State())
                .messages(input.messages().stream()
                        .map(message -> switch (message.payload()) {
                            case Message.Payload.User userPayload -> {
                                final UserMessage userMessage = new UserMessage();
                                userMessage.setId(String.valueOf(message.id()));
                                userMessage.setContent(userPayload.content());
                                yield userMessage;
                            }

                            case Message.Payload.Bot botPayload -> {
                                final AssistantMessage assistantMessage = new AssistantMessage();
                                assistantMessage.setId(String.valueOf(message.id()));
                                assistantMessage.setContent(botPayload.content());
                                yield assistantMessage;
                            }
                        })
                        .toList()
                )
                .build();
    }

}
