package bg.dalexiev.grumpysenior.auth.api.dto;

public record LoginRequest(
        String username,
        String password
) {
}
