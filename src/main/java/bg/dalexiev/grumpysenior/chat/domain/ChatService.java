package bg.dalexiev.grumpysenior.chat.domain;

import bg.dalexiev.grumpysenior.chat.persistence.ConversationEntity;
import bg.dalexiev.grumpysenior.chat.persistence.ConversationRepository;
import bg.dalexiev.grumpysenior.chat.persistence.MessageRepository;
import bg.dalexiev.grumpysenior.util.Either;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.util.List;

@Service
public class ChatService {

    public sealed interface Error {

        record InvalidOwner(long userId) implements Error {

        }

        record InvalidConversation(long conversationId) implements Error {

        }

    }

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    private final Clock clock;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, ObjectMapper objectMapper, Clock clock) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
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
                        final List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedAtDesc(conversationId).stream()
                                .map(entity -> Message.fromEntity(entity, objectMapper))
                                .toList();
                        return Either.right(messages);
                    } else {
                        return Either.left(new Error.InvalidOwner(userId));
                    }
                })
                .orElseGet(() -> Either.left(new Error.InvalidConversation(conversationId)));
    }
}
