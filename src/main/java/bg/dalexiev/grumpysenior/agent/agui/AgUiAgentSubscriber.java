package bg.dalexiev.grumpysenior.agent.agui;

import bg.dalexiev.grumpysenior.chat.domain.AIGateway;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.agui.core.agent.AgentSubscriber;
import com.agui.core.agent.AgentSubscriberParams;
import com.agui.core.event.BaseEvent;
import com.agui.core.event.CustomEvent;
import com.agui.core.event.RunFinishedEvent;
import com.agui.core.event.StateSnapshotEvent;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class AgUiAgentSubscriber implements AgentSubscriber {

    private final AIGateway.Subscriber subscriber;
    private final JsonMapper jsonMapper;

    private AgUiAgentSubscriber(AIGateway.Subscriber subscriber, JsonMapper jsonMapper) {
        this.subscriber = subscriber;
        this.jsonMapper = jsonMapper;
    }

    public static AgUiAgentSubscriber from(AIGateway.Subscriber subscriber, JsonMapper jsonMapper) {
        return new AgUiAgentSubscriber(subscriber, jsonMapper);
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof StateSnapshotEvent) {
            onStateSnapshotEvent((StateSnapshotEvent) event);
            return;
        }

        subscriber.onEventSerialized(jsonMapper.writeValueAsString(event));
    }

    @Override
    public void onRunFinishedEvent(RunFinishedEvent event) {
        subscriber.onRunFinished();
    }

    @Override
    public void onRunFailed(AgentSubscriberParams params, Throwable error) {
        subscriber.onRunFailed(error);
    }

    @Override
    public void onStateSnapshotEvent(StateSnapshotEvent event) {
        subscriber.onStateChanged(event.getState().getState());
    }

    @Override
    public void onCustomEvent(CustomEvent event) {
        final Message.Payload payload = jsonMapper.treeToValue((JsonNode) event.getRawEvent(), Message.Payload.class);
        subscriber.onMessagePayloadReady(payload);
    }
}
