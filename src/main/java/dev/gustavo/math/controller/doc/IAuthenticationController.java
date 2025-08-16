package dev.gustavo.math.controller.doc;

import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.LoginResponseDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.infra.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

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
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    UserResponseDTO register(@Valid @RequestBody UserRequestDTO userCreateRequest);


    @Operation(
            summary = "Login with username and password",
            description = "Public endpoint to authenticate and receive a JWT token",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    })
    LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest);

}
