package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.advice.ErrorResponseDTO;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Ranking", description = "View user ranking by solved problems")
public interface IRankingController {

    @Operation(
            summary = "List user ranking by distinct solved problems",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    PageableResponseDTO<RankingResponseDTO> findRanking(
            @Parameter(description = "Page number, starting at 0", example = "0")
            Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            Integer size,
            @Parameter(description = "Optional difficulty filter", example = "HARD")
            ProblemDifficulty difficulty);
}
