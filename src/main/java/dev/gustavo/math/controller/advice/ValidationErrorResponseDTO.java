package dev.gustavo.math.controller.advice;

import java.util.Map;

public record ValidationErrorResponseDTO(
        String message,
        Map<String, String> errors
) {
}
