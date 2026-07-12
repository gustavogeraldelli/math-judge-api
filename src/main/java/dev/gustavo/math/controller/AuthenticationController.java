package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.IAuthenticationController;
import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.LoginResponseDTO;
import dev.gustavo.math.controller.dto.user.RefreshTokenRequestDTO;
import dev.gustavo.math.controller.dto.user.UserCreateRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements IAuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody UserCreateRequestDTO userCreateRequest) {
        var user = authenticationService.register(userMapper.toUser(userCreateRequest));
        return userMapper.toUserResponseDTO(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        var tokens = authenticationService.login(userMapper.toUser(loginRequest));
        return new LoginResponseDTO(tokens.accessToken(), tokens.refreshToken(), tokens.tokenType(), tokens.expiresIn());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        var tokens = authenticationService.refresh(refreshTokenRequest.refreshToken());
        return new LoginResponseDTO(tokens.accessToken(), tokens.refreshToken(), tokens.tokenType(), tokens.expiresIn());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        authenticationService.logout(refreshTokenRequest.refreshToken());
    }

}
