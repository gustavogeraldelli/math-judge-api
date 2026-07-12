package dev.gustavo.math.service;

public record AuthenticationTokens(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
