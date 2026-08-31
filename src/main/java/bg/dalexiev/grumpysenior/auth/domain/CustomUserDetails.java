package bg.dalexiev.grumpysenior.auth.domain;

import bg.dalexiev.grumpysenior.user.persistence.UserEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;

    private CustomUserDetails(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static CustomUserDetails from(UserEntity user) {
        return new CustomUserDetails(user.id(), user.username(), user.password());
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getUsername() {
        return username;
    }
}
