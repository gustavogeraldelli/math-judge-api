package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.LoginResponseDTO;
import dev.gustavo.math.controller.dto.user.RefreshTokenRequestDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user and administrator authentication")
public interface IAuthenticationController {

    @Operation(
            summary = "Register a new user",
            description = "Public endpoint to create a new user account",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    UserResponseDTO register(UserRequestDTO userCreateRequest);


    @Operation(
            summary = "Login with username and password",
            description = "Public endpoint to authenticate and receive access and refresh tokens",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    })
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    @Operation(
            summary = "Refresh access token",
            description = "Public endpoint to exchange a valid refresh token for a new access token",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    })
    LoginResponseDTO refresh(RefreshTokenRequestDTO refreshTokenRequest);

    @Operation(
            summary = "Logout",
            description = "Public endpoint to revoke the current refresh token",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Refresh token revoked"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    })
    void logout(RefreshTokenRequestDTO refreshTokenRequest);

}
