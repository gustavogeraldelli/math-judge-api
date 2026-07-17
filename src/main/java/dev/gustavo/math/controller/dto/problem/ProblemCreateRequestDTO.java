package dev.gustavo.math.controller.dto.problem;

import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request body for creating a Problem")
public record ProblemCreateRequestDTO(
        @Schema(description = "Title of the problem", example = "Find the derivative")
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 64, message = "Title can have up to 64 characters")
        String title,

        @Schema(description = "Detailed description of the problem", example = "Given f(x) = 2^x, find f'(x)")
        @NotBlank(message = "Description is required")
        String description,

        @Schema(description = "Difficulty level (EASY, MEDIUM or HARD)", example = "EASY")
        @NotNull(message = "Difficulty is required")
        ProblemDifficulty difficulty,

        @Schema(description = "Problem type (NUMERIC or EXPRESSION)", example = "NUMERIC")
        @NotNull(message = "Type is required")
        ProblemType type,

        @Schema(description = "Valid variable names for expression problems", example = "[\"x\"]")
        List<String> variables
) {
}
