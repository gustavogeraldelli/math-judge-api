package dev.gustavo.math.service.auth;

public record AuthenticationTokens(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
