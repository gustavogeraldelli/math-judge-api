package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body after successful login, containing access and refresh tokens")
public record LoginResponseDTO(
        @Schema(description = "JWT access token for authenticated requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Refresh token used to request a new access token", example = "a-long-random-refresh-token")
        String refreshToken,

        @Schema(description = "Token type used in the Authorization header", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token lifetime in seconds", example = "1800")
        long expiresIn
) {
}
