package dev.gustavo.math.controller.dto.submission;

import dev.gustavo.math.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body representing a Submission")
public record SubmissionResponseDTO(
        @Schema(description = "ID of the challenge being solved", type = "long", example = "1")
        Long challenge,

        @Schema(description = "Result status of the submission", example = "ACCEPTED")
        SubmissionStatus status,

        @Schema(description = "Timestamp of when the submission was made", type = "timestamp", example = "2025-08-15T14:30:00")
        LocalDateTime submittedAt
) {
}
