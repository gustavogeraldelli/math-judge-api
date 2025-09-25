package dev.gustavo.math.controller.dto.testcase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating or updating a Test Case")
public record TestCaseRequestDTO(
        @Schema(description = "ID of the problem this test case belongs to", type = "long", example = "1")
        @NotNull(message = "Problem id is required")
        Long problem,

        @Schema(description = "Variable values value for the test case", type = "string", example = "10")
        @NotNull(message = "Variable values cannot be null")
        String variableValues,

        @Schema(description = "Expected output for the test case", type = "string", example = "20")
        @NotBlank(message = "Expected output is required")
        String expectedAnswer
) {
}
