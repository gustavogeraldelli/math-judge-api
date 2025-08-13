package dev.gustavo.math.controller.dto.submission;

import dev.gustavo.math.entity.enums.SubmissionStatus;

import java.time.LocalDateTime;

public record SubmissionResponseDTO(
        Long challenge,
        SubmissionStatus status,
        LocalDateTime submittedAt
) {
}
