package bg.dalexiev.grumpysenior.chat.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends ListCrudRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByConversationIdOrderByCreatedAtDesc(long conversationId);

}
