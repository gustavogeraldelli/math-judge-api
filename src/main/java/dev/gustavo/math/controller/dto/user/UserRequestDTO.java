package dev.gustavo.math.controller.dto.user;

public record UserRequestDTO(
        String username,
        String password,
        String nickname
) {
}
