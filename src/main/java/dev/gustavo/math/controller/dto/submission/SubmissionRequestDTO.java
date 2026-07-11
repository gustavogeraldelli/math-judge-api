package dev.gustavo.math.controller.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a Submission")
public record SubmissionRequestDTO(
        @Schema(description = "ID of the problem being solved", type = "long", example = "1")
        @NotNull(message = "Problem id is required")
        Long problem,

        @Schema(description = "Submitted mathematical answer", example = "2x")
        @NotBlank(message = "Answer is required")
        String answer
) {
}
