package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeRequestDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeSubmissionsResponseDTO;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Challenges", description = "Manage and view math challenges")
public interface IChallengeController {


    @Operation(
            summary = "List all challenges",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    PageableResponseDTO<ChallengeResponseDTO> findAll(Integer page, Integer size);

    @Operation(
            summary = "Find challenge by ID",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Challenge found",
                    content = @Content(schema = @Schema(implementation = ChallengeResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ChallengeResponseDTO findById(Long id);

    @Operation(
            summary = "Create a new challenge (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Challenge created successfully",
                    content = @Content(schema = @Schema(implementation = ChallengeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    ChallengeResponseDTO create(ChallengeRequestDTO challengeCreateRequest);

    @Operation(
            summary = "Update challenge (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Challenger updated successfully",
                    content = @Content(schema = @Schema(implementation = ChallengeResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ChallengeResponseDTO update(Long id, ChallengeRequestDTO challengeUpdateRequest);

    @Operation(
            summary = "Delete challenge (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Challenge deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(Long id);

    @Operation(
            summary = "List all submissions for a challenge (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    PageableResponseDTO<ChallengeSubmissionsResponseDTO> listChallengeSubmissions(Long id, Integer page, Integer size);
}
