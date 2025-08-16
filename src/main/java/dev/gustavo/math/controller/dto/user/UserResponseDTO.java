package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body representing a user")
public record UserResponseDTO(
        @Schema(description = "Username of the user", type = "string", example = "username")
        String username,

        @Schema(description = "Nickname of the user", type = "string", example = "Johnny")
        String nickname
) {
}
