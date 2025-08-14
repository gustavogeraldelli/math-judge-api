package dev.gustavo.math.controller.dto.testcase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestCaseRequestDTO(
        @NotNull(message = "Challenge id is required")
        Long challenge,

        @NotBlank(message = "Input is required")
        String input,

        @NotBlank(message = "Expected output is required")
        String expectedOutput
) {
}
