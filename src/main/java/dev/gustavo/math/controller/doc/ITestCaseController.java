package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseCreateRequestDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseUpdateRequestDTO;
import dev.gustavo.math.controller.advice.ErrorResponseDTO;
import dev.gustavo.math.controller.advice.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test Cases", description = "Manage test cases for problems (admin only)")
public interface ITestCaseController {

    @Operation(
            summary = "List test cases for a problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test cases retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    PageableResponseDTO<TestCaseResponseDTO> listByProblem(Long problemId, Integer page, Integer size);

    @Operation(
            summary = "Create a test case for a problem (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Test case created successfully",
                    content = @Content(schema = @Schema(implementation = TestCaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    TestCaseResponseDTO create(Long problemId, TestCaseCreateRequestDTO testCaseCreateRequest);

    @Operation(
            summary = "Update test case (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test case updated successfully",
                    content = @Content(schema = @Schema(implementation = TestCaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    TestCaseResponseDTO update(Long id, TestCaseUpdateRequestDTO testCaseUpdateRequest);

    @Operation(
            summary = "Delete test case (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Test case deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(Long id);

}
