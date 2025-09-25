package dev.gustavo.math.controller.dto.user;

import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body representing a submission made by a user to a problem")
public record UserSubmissionsResponseDTO(
        @Schema(description = "Problem related to the submission")
        ProblemResponseDTO problem,

        @Schema(description = "Submitted answer", type = "string", example = "2x")
        String answer,

        @Schema(description = "Result status", example = "ACCEPTED")
        SubmissionStatus status,

        @Schema(description = "Timestamp of when the submission was made", type = "timestamp", example = "2025-08-15T14:30:00")
        LocalDateTime submittedAt
) {
}
