package bg.dalexiev.grumpysenior.chat.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("messages")
public record MessageEntity(
        @Id @Column("id") Long id,
        @Column("conversation_id") Long conversationId,
        @Column("type") Type type,
        @Column("payload") String payload,
        @Column("created_at") Instant createdAt
) {

        public enum Type {
                USER, BOT
        }

}
