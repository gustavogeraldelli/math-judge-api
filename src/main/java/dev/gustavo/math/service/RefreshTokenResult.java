package dev.gustavo.math.service;

import dev.gustavo.math.entity.User;

public record RefreshTokenResult(User user, String refreshToken) {
}
