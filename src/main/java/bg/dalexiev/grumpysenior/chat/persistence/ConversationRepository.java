package bg.dalexiev.grumpysenior.chat.persistence;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends ListCrudRepository<ConversationEntity, Long> {

    @Query("select distinct c.id, c.user_id, c.title, c.created_at from conversations c join messages m on c.id = m.conversation_id where c.user_id = :userId order by c.created_at desc")
    List<ConversationEntity> findAllNonEmptyByUserIdOrderByCreatedAtDesc(long userId);

}
