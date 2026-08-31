package bg.dalexiev.grumpysenior.user.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record UserEntity(
        @Id @Column("id") Long id,
        @Column("username") String username,
        @Column("password") String password
) {
}
