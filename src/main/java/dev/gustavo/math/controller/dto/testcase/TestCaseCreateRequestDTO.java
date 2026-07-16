package dev.gustavo.math.controller.dto.testcase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a Test Case")
public record TestCaseCreateRequestDTO(
        @Schema(description = "Variable values for the test case", type = "string", example = "10")
        @NotNull(message = "Variable values cannot be null")
        String variableValues,

        @Schema(description = "Expected output for the test case", type = "string", example = "20")
        @NotBlank(message = "Expected output is required")
        String expectedAnswer
) {
}
