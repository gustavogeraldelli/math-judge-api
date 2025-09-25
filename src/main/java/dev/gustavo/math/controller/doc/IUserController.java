package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.controller.dto.user.UserSubmissionsResponseDTO;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Users", description = "User management and submissions")
public interface IUserController {

    @Operation(
            summary = "List all users (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content)
    })
    PageableResponseDTO<UserResponseDTO> findAll(Integer page, Integer size);

    @Operation(
            summary = "Find user by ID (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    UserResponseDTO findById(UUID id);

    @Operation(
            summary = "Update user (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    UserResponseDTO update(UUID id, UserRequestDTO userUpdateRequest);

    @Operation(
            summary = "Delete user (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(UUID id);

    @Operation(
            summary = "List all submissions from a user (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissions(UUID id, Integer page, Integer size);

    @Operation(
            summary = "List submissions of a user in a specific problem (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User or problem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissionsInProblem(UUID userId, Long problemId, Integer page, Integer size);

}
