package dev.gustavo.math.controller.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request body for creating a Submission")
public record SubmissionRequestDTO(
        @Schema(description = "ID of the challenge being solved", type = "long", example = "1")
        @NotNull(message = "Challenge id is required")
        Long challenge,

        @Schema(description = "ID of the user making the submission", type = "uuid", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "User id is required")
        UUID user,

        @Schema(description = "Submitted mathematical expression", example = "2x")
        @NotBlank(message = "Expression is required")
        String expression
) {
}
