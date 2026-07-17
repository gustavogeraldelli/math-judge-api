package dev.gustavo.math.controller.dto.problem;

import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request body for partially updating a Problem")
public record ProblemUpdateRequestDTO(
        @Schema(description = "New title of the problem", example = "Find the second derivative")
        @Size(min = 1, max = 64, message = "Title can have up to 64 characters")
        String title,

        @Schema(description = "New detailed description of the problem", example = "Given f(x) = x^3, find f''(x)")
        String description,

        @Schema(description = "New difficulty level (EASY, MEDIUM or HARD)", example = "MEDIUM")
        ProblemDifficulty difficulty,

        @Schema(description = "New problem type (NUMERIC or EXPRESSION)", example = "EXPRESSION")
        ProblemType type,

        @Schema(description = "Valid variable names for expression problems", example = "[\"x\", \"y\"]")
        List<String> variables
) {
}
