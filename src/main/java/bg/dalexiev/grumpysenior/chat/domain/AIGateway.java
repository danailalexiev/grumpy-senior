package bg.dalexiev.grumpysenior.chat.domain;

import java.util.List;
import java.util.Map;

public interface AIGateway {

    record Input(
            Map<String, Object> params,
            List<Message> messages
    ) {

        public Input {
            params = Map.copyOf(params);
            messages = List.copyOf(messages);
        }

    }

    interface Subscriber {

        void onMessagePayloadReady(Message.Payload payload);

        void onStateChanged(Map<String, Object> state);

        void onEventSerialized(String serializedEvent);

        void onRunFinished();

        void onRunFailed(Throwable throwable);

    }

    void runAI(Input input, Subscriber subscriber);

}
