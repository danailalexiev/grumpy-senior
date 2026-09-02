package bg.dalexiev.grumpysenior.chat.api.sse;

import bg.dalexiev.grumpysenior.chat.domain.ChatService;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;

public class AnswerStreamObserver implements ChatService.StreamObserver {

    private final SseEmitter sseEmitter;

    private AnswerStreamObserver(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    public static AnswerStreamObserver boundTo(@NonNull SseEmitter sseEmitter) {
        return new AnswerStreamObserver(Objects.requireNonNull(sseEmitter, "sseEmitter is required"));
    }

    @Override
    public void onNext(String serializedEvent) {
        try {
            sseEmitter.send(SseEmitter.event().data(serializedEvent));
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }
    }

    @Override
    public void onComplete() {
        sseEmitter.complete();
    }

    @Override
    public void onError(Throwable throwable) {
        sseEmitter.completeWithError(throwable);
    }

}
