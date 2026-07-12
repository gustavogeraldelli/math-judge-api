package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for partially updating a user")
public record UserUpdateRequestDTO(
        @Schema(description = "New username of the user", type = "string", example = "newUsername")
        @Size(min = 4, max = 64, message = "Username must have between 4 and 64 characters")
        String username,

        @Schema(description = "New password of the user", type = "string", example = "newPassword")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,

        @Schema(description = "New nickname of the user", type = "string", example = "Johnny")
        String nickname
) {
}
