package bg.dalexiev.grumpysenior.agent.codereview.agui;

import bg.dalexiev.grumpysenior.agent.mapper.MessagePayloadMapper;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import bg.dalexiev.grumpysenior.chat.domain.ai.AIGateway;
import com.agui.core.agent.RunAgentParameters;
import com.agui.core.message.AssistantMessage;
import com.agui.core.message.UserMessage;
import com.agui.core.state.State;
import org.jspecify.annotations.NonNull;

public class InputMapper {

    private final MessagePayloadMapper messagePayloadMapper;

    private InputMapper(MessagePayloadMapper messagePayloadMapper) {
        this.messagePayloadMapper = messagePayloadMapper;
    }

    public static InputMapper newInstance() {
        return new InputMapper(MessagePayloadMapper.newInstance());
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
                                userMessage.setContent(messagePayloadMapper.toAiReadableContent(userPayload));
                                yield userMessage;
                            }

                            case Message.Payload.Bot botPayload -> {
                                final AssistantMessage assistantMessage = new AssistantMessage();
                                assistantMessage.setId(String.valueOf(message.id()));
                                assistantMessage.setContent(messagePayloadMapper.toAiReadableContent(botPayload));
                                yield assistantMessage;
                            }
                        })
                        .toList()
                )
                .build();
    }

}
