package dev.gustavo.math.controller.dto.problem;

import dev.gustavo.math.entity.enums.ProblemDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body representing a Problem")
public record ProblemResponseDTO(
        @Schema(description = "Unique identifier of the problem", type = "long", example = "1")
        Long id,

        @Schema(description = "Title of the problem", type = "string", example = "Find the derivative")
        String title,

        @Schema(description = "Detailed description of the problem", type = "string", example = "Given f(x) = 2^x, find f'(x)")
        String description,

        @Schema(description = "Difficulty level", example = "EASY")
        ProblemDifficulty difficulty
) {
}
