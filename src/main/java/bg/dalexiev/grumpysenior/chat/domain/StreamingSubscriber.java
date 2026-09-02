package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.domain.ai.AIGateway;

import java.util.Map;
import java.util.function.Consumer;

public class StreamingSubscriber implements AIGateway.Subscriber {

    private final ChatService.StreamObserver streamObserver;
    private final Consumer<Message.Payload.Bot> onMessagePayloadReady;
    private final Consumer<Map<String, Object>> onStateChanged;

    private StreamingSubscriber(ChatService.StreamObserver streamObserver, Consumer<Message.Payload.Bot> onMessagePayloadReady, Consumer<Map<String, Object>> onStateChanged) {
        this.streamObserver = streamObserver;
        this.onMessagePayloadReady = onMessagePayloadReady;
        this.onStateChanged = onStateChanged;
    }

    static StreamingSubscriber create(ChatService.StreamObserver streamObserver, Consumer<Message.Payload.Bot> onMessagePayloadReady, Consumer<Map<String, Object>> onStateChanged) {
        return new StreamingSubscriber(streamObserver, onMessagePayloadReady, onStateChanged);
    }

    @Override
    public void onMessagePayloadReady(Message.Payload.Bot payload) {
        onMessagePayloadReady.accept(payload);
    }

    @Override
    public void onStateChanged(Map<String, Object> state) {
        onStateChanged.accept(state);
    }

    @Override
    public void onEventSerialized(String serializedEvent) {
        streamObserver.onNext(serializedEvent);
    }

    @Override
    public void onRunFinished() {
        streamObserver.onComplete();
    }

    @Override
    public void onRunFailed(Throwable throwable) {
        streamObserver.onError(throwable);
    }
}
