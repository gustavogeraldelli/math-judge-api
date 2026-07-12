package dev.gustavo.math.controller.dto.testcase;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for partially updating a Test Case")
public record TestCaseUpdateRequestDTO(
        @Schema(description = "New problem ID for this test case", type = "long", example = "1")
        Long problem,

        @Schema(description = "New variable values for the test case", type = "string", example = "10")
        String variableValues,

        @Schema(description = "New expected output for the test case", type = "string", example = "20")
        String expectedAnswer
) {
}
