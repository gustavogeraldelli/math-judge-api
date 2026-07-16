package dev.gustavo.math.controller.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for creating a Submission")
public record SubmissionRequestDTO(
        @Schema(description = "Submitted mathematical answer", example = "2x")
        @NotBlank(message = "Answer is required")
        String answer
) {
}
