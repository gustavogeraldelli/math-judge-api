package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.controller.advice.ErrorResponseDTO;
import dev.gustavo.math.controller.advice.ValidationErrorResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Tag(name = "Submissions", description = "Submit and view problem submissions")
public interface ISubmissionController {

    @Operation(
            summary = "List submissions with optional filters",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    PageableResponseDTO<SubmissionResponseDTO> findAll(
            @Parameter(description = "Page number, starting at 0", example = "0")
            Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            Integer size,
            @Parameter(description = "Filter by user ID. Regular users can only use their own ID.")
            UUID userId,
            @Parameter(description = "Filter by problem ID", example = "1")
            Long problemId,
            @Parameter(description = "Filter by submission status", example = "ACCEPTED")
            SubmissionStatus status,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "Find submission by ID (owner or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission found",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Submission not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    SubmissionResponseDTO findById(Long id, @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "Create a new submission for a problem",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Submission created successfully",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    SubmissionResponseDTO create(
            @Parameter(description = "Problem ID", example = "1")
            Long problemId,
            SubmissionRequestDTO submissionCreateRequest,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "Delete submission (admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Submission deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Submission not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(Long id);
}
