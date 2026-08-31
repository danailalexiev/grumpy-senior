package bg.dalexiev.grumpysenior.chat.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("conversations")
public record ConversationEntity(
        @Id @Column("id") Long id,
        @Column("user_id") long userId,
        @Column("title") String title,
        @Column("created_at") Instant createdAt
) {

    public static final String DEFAULT_TITLE = "New Chat";

    public static ConversationEntity empty(long userId, Instant createdAt) {
        return new ConversationEntity(null, userId, DEFAULT_TITLE, createdAt);
    }
}
