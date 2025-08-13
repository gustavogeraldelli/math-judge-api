package dev.gustavo.math.controller.dto.testcase;

public record TestCaseRequestDTO(
        Long challenge,
        String input,
        String expectedOutput
) {
}
