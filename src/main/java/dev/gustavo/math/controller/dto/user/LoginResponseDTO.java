package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body after successful login, containing JWT token")
public record LoginResponseDTO(
        @Schema(description = "JWT token for authenticated requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
