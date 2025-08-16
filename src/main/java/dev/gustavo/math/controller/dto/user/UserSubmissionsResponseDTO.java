package dev.gustavo.math.controller.dto.user;

import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body representing a submission made by a user to a challenge")
public record UserSubmissionsResponseDTO(
        @Schema(description = "Challenge related to the submission")
        ChallengeResponseDTO challenge,

        @Schema(description = "Submitted mathematical expression", type = "string", example = "2x")
        String expression,

        @Schema(description = "Result status of the submission", example = "ACCEPTED")
        SubmissionStatus status,

        @Schema(description = "Timestamp of when the submission was made", type = "timestamp", example = "2025-08-15T14:30:00")
        LocalDateTime submittedAt
) {
}
