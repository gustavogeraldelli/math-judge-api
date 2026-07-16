package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.controller.dto.user.UserUpdateRequestDTO;
import dev.gustavo.math.controller.advice.ErrorResponseDTO;
import dev.gustavo.math.controller.advice.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Users", description = "User management")
public interface IUserController {

    @Operation(
            summary = "List all users (admin only)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                content = @Content(schema = @Schema(implementation = PageableResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content)
    })
    PageableResponseDTO<UserResponseDTO> findAll(
            @Parameter(description = "Page number, starting at 0", example = "0")
            Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            Integer size);

    @Operation(
            summary = "Find user by ID (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
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
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    UserResponseDTO update(UUID id, UserUpdateRequestDTO userUpdateRequest);

    @Operation(
            summary = "Delete user (self or admin)",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired access token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    void delete(UUID id);

}
