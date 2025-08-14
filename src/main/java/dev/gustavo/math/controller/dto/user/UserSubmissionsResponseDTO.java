package dev.gustavo.math.controller.dto.user;

import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;

import java.time.LocalDateTime;

public record UserSubmissionsResponseDTO(
        ChallengeResponseDTO challenge,
        String expression,
        SubmissionStatus status,
        LocalDateTime submittedAt
) {
}
