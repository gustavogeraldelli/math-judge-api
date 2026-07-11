package dev.gustavo.math.controller.dto.testcase;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body representing a Test Case")
public record TestCaseResponseDTO(
        @Schema(description = "Unique identifier of the test case", type = "long", example = "1")
        Long id,

        @Schema(description = "ID of the problem this test case belongs to", type = "long", example = "1")
        Long problem,

        @Schema(description = "Variable values for the test case", type = "string", example = "10")
        String variableValues,

        @Schema(description = "Expected output for the test case", type = "string", example = "20")
        String expectedAnswer
) {
}
