package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.domain.ai.AIGateway;
import bg.dalexiev.grumpysenior.chat.domain.event.FirstTurnCompletedEvent;
import bg.dalexiev.grumpysenior.chat.persistence.ConversationEntity;
import bg.dalexiev.grumpysenior.chat.persistence.ConversationRepository;
import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import bg.dalexiev.grumpysenior.chat.persistence.MessageRepository;
import bg.dalexiev.grumpysenior.util.Either;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ChatService {

    public sealed interface Error {

        record InvalidOwner(long userId) implements Error {

        }

        record InvalidConversation(long conversationId) implements Error {

        }

    }

    public interface StreamObserver {

        void onNext(String serializedEvent);

        void onComplete();

        void onError(Throwable throwable);

    }

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    private final JsonMapper jsonMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final AIGateway aiGateway;

    private final Clock clock;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, JsonMapper jsonMapper, ApplicationEventPublisher eventPublisher, AIGateway aiGateway, Clock clock) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.jsonMapper = jsonMapper;
        this.eventPublisher = eventPublisher;
        this.aiGateway = aiGateway;
        this.clock = clock;
    }

    public Conversation create(long userId) {
        final ConversationEntity entity = conversationRepository.save(ConversationEntity.empty(userId, clock.instant()));
        return Conversation.fromEntity(entity);
    }

    public List<Conversation> getAllConversationsByUser(long userId) {
        return conversationRepository.findAllNonEmptyByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(Conversation::fromEntity)
                .toList();
    }

    public Either<Error, List<Message>> listMessagesForConversation(long userId, long conversationId) {
        return conversationRepository.findById(conversationId)
                .<Either<Error, List<Message>>>map(conversation -> {
                    if (conversation.userId() == userId) {
                        final List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                                .map(entity -> Message.fromEntity(entity, jsonMapper))
                                .toList();
                        return Either.right(messages);
                    } else {
                        return Either.left(new Error.InvalidOwner(userId));
                    }
                })
                .orElseGet(() -> Either.left(new Error.InvalidConversation(conversationId)));
    }

    public Either<Error, Void> generateAnswerAsync(long userId, long conversationId, Message.Payload.User input, StreamObserver streamObserver) {
        return getConversation(userId, conversationId)
                .flatMap(conversation -> {
                    runAgent(conversation, input, streamObserver);
                    return Either.right(null);
                });
    }

    private @NonNull Either<Error, ConversationEntity> getConversation(long userId, long conversationId) {
        return conversationRepository.findById(conversationId)
                .<Either<Error, ConversationEntity>>map(conversation -> {
                    if (Objects.equals(conversation.userId(), userId)) {
                        return Either.right(conversation);
                    } else {
                        return Either.left(new Error.InvalidOwner(userId));
                    }
                })
                .orElseGet(() -> Either.left(new Error.InvalidConversation(conversationId)));
    }

    private void runAgent(ConversationEntity conversation, Message.Payload.User input, StreamObserver streamObserver) {
        final MessageEntity message = messageRepository.save(MessageEntity.newInstance(conversation.id(), MessageEntity.Type.USER, jsonMapper.writeValueAsString(input), clock.instant()));

        final List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversation.id()).stream()
                .map(entity -> Message.fromEntity(entity, jsonMapper))
                .toList();

        final AIGateway.Input agentInput = new AIGateway.Input(
                Map.of(
                        "conversationId", conversation.id().toString(),
                        "messageId", message.id().toString()
                ),
                messages
        );

        final AIGateway.Subscriber subscriber = StreamingSubscriber.create(
                streamObserver,
                payload -> {
                    saveBotMessage(conversation, payload);
                    publishFirstTurnCompletedEventIfNeeded(conversation, messages.size(), input, payload);
                },
                _ -> { /* do nothing */ }
        );

        aiGateway.runAI(agentInput, subscriber);
    }

    private void saveBotMessage(ConversationEntity conversation, Message.Payload payload) {
        final MessageEntity answer = MessageEntity.newInstance(conversation.id(), MessageEntity.Type.BOT, jsonMapper.writeValueAsString(payload), clock.instant());
        messageRepository.save(answer);
    }

    private void publishFirstTurnCompletedEventIfNeeded(ConversationEntity conversation, int messageCount, Message.Payload.User userPrompt, Message.Payload.Bot botAnswer) {
        if ((messageCount == 1) && conversation.title().equals(ConversationEntity.DEFAULT_TITLE)) {
            final FirstTurnCompletedEvent event = new FirstTurnCompletedEvent(conversation.id(), userPrompt.content(), botAnswer.content());
            eventPublisher.publishEvent(event);
        }
    }
}
