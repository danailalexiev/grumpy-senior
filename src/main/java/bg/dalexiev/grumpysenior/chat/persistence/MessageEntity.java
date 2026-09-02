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
        @Column("payload") SerializedPayload payload,
        @Column("created_at") Instant createdAt
) {

    public enum Type {
        USER, BOT
    }

    public record SerializedPayload(String value) {}

    public static MessageEntity newInstance(long conversationId, Type type, String payload, Instant createdAt) {
            return new MessageEntity(null, conversationId, type, new SerializedPayload(payload), createdAt);
    }

}
