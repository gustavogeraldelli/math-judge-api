package dev.gustavo.math.controller.dto.submission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmissionRequestDTO(
        @NotNull(message = "Challenge id is required")
        Long challenge,

        @NotNull(message = "User id is required")
        UUID user,

        @NotBlank(message = "Expression is required")
        String expression
) {
}
