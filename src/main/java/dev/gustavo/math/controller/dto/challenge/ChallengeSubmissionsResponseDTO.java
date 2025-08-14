package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;

import java.time.LocalDateTime;

public record ChallengeSubmissionsResponseDTO(
        UserResponseDTO user,
        String expression,
        SubmissionStatus status,
        LocalDateTime submittedAt
) {
}
