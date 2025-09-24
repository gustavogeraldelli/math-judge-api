package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemRequestDTO;
import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemSubmissionsResponseDTO;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Problems", description = "Manage and view math challenges")
public interface ProblemController {


    @Operation(
            summary = "List all problems",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    PageableResponseDTO<ProblemResponseDTO> findAll(Integer page, Integer size);

    @Operation(
            summary = "Find problem by ID",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem found",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ProblemResponseDTO findById(Long id);

    @Operation(
            summary = "Create a new problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Problem created successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    ProblemResponseDTO create(ProblemRequestDTO problemCreateRequest);

    @Operation(
            summary = "Update problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problemr updated successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ProblemResponseDTO update(Long id, ProblemRequestDTO problemUpdateRequest);

    @Operation(
            summary = "Delete problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Problem deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(Long id);

    @Operation(
            summary = "List all submissions for a problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    PageableResponseDTO<ProblemSubmissionsResponseDTO> listProblemSubmissions(Long id, Integer page, Integer size);
}
