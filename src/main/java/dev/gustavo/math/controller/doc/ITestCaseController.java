package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseRequestDTO;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Test Cases", description = "Manage test cases for problems (admin only)")
public interface ITestCaseController {

    @Operation(
            summary = "Create a test case (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Test case created successfully",
                    content = @Content(schema = @Schema(implementation = TestCaseRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    TestCase create(TestCaseRequestDTO testCaseCreateRequest);

    @Operation(
            summary = "Update test case (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test case updated successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    TestCase update(Long id, TestCaseRequestDTO testCaseUpdateRequest);

    @Operation(
            summary = "Delete test case (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Test case deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(Long id);

}
