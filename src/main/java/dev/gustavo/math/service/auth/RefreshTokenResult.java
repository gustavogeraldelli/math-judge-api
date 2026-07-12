package dev.gustavo.math.service.auth;

import dev.gustavo.math.entity.User;

public record RefreshTokenResult(User user, String refreshToken) {
}
