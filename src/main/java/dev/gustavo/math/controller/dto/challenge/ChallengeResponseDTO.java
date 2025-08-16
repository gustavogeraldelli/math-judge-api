package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body representing a Challenge")
public record ChallengeResponseDTO(
        @Schema(description = "Unique identifier of the challenge", type = "long", example = "1")
        Long id,

        @Schema(description = "Title of the challenge", type = "string", example = "Find the derivative")
        String title,

        @Schema(description = "Detailed description of the challenge", type = "string", example = "Given f(x) = 2^x, find f'(x)")
        String description,

        @Schema(description = "Difficulty level", example = "EASY")
        ChallengeDifficulty difficulty
) {
}
