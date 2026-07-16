package dev.gustavo.math.controller.dto.ranking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "User ranking entry")
public record RankingResponseDTO(
        @Schema(description = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId,

        @Schema(description = "Username", example = "john")
        String username,

        @Schema(description = "User nickname", example = "Johnny")
        String nickname,

        @Schema(description = "Number of distinct accepted problems", example = "12")
        Long resolvedProblems
) {
}
