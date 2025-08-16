package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating or updating a user")
public record UserRequestDTO(
        @Schema(description = "Username of the user", type = "string", example = "username")
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 64, message = "Username must have between 4 and 64 characters")
        String username,

        @Schema(description = "Password of the user", type = "string", example = "password")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,

        @Schema(description = "Optional nickname of the user", type = "string", example = "Johnny")
        String nickname
) {
}
