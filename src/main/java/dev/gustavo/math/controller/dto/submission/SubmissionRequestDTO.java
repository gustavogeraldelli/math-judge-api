package dev.gustavo.math.controller.dto.submission;

import java.util.UUID;

public record SubmissionRequestDTO(
        Long challenge,
        UUID user,
        String expression
) {
}
