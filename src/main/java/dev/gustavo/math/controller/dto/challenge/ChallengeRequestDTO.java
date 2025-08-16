package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating or updating a Challenge")
public record ChallengeRequestDTO(
        @Schema(description = "Title of the challenge", example = "Find the derivative")
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 64, message = "Title can have up to 64 characters")
        String title,

        @Schema(description = "Detailed description of the challenge", example = "Given f(x) = 2^x, find f'(x)")
        @NotBlank(message = "Description is required")
        String description,

        @Schema(description = "Difficulty level (EASY, MEDIUM or HARD)", example = "EASY")
        @NotNull(message = "Difficulty is required")
        ChallengeDifficulty difficulty
) {
}
