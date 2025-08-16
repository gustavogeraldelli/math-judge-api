package dev.gustavo.math.controller.dto.testcase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating or updating a Test Case")
public record TestCaseRequestDTO(
        @Schema(description = "ID of the challenge this test case belongs to", type = "long", example = "1")
        @NotNull(message = "Challenge id is required")
        Long challenge,

        @Schema(description = "Input value for the test case", type = "string", example = "10")
        @NotBlank(message = "Input is required")
        String input,

        @Schema(description = "Expected output for the test case", type = "string", example = "20")
        @NotBlank(message = "Expected output is required")
        String expectedOutput
) {
}
