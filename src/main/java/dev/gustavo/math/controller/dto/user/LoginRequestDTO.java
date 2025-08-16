package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user login")
public record LoginRequestDTO(
        @Schema(description = "Username of the user", type = "string", example = "username")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Password of the user", type = "string", example = "password")
        @NotBlank(message = "Password is required")
        String password
) {
}
