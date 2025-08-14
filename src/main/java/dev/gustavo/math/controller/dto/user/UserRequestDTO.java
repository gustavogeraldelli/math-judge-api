package dev.gustavo.math.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 64, message = "Username must have between 4 and 64 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,

        String nickname
) {
}
