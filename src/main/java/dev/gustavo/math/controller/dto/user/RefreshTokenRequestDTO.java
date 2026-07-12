package dev.gustavo.math.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body containing a refresh token")
public record RefreshTokenRequestDTO(
        @Schema(description = "Refresh token returned by login or refresh", example = "a-long-random-refresh-token")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
