package dev.gustavo.math.controller.dto.problem;

import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body representing a submission in the context of a Problem")
public record ProblemSubmissionsResponseDTO(
        @Schema(description = "User who made the submission")
        UserResponseDTO user,

        @Schema(description = "Submitted answer", type = "string", example = "2x")
        String answer,

        @Schema(description = "Status of the submission", example = "ACCEPTED")
        SubmissionStatus status,

        @Schema(description = "Timestamp of submission", type = "timestamp", example = "2025-08-15T14:30:00")
        LocalDateTime submittedAt
) {
}
