package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.IAuthenticationController;
import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.LoginResponseDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements IAuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO userCreateRequest) {
        var user = authenticationService.register(UserMapper.INSTANCE.toUser(userCreateRequest));
        return UserMapper.INSTANCE.toUserResponseDTO(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        var token = authenticationService.login(UserMapper.INSTANCE.toUser(loginRequest));
        return new LoginResponseDTO(token);
    }

}
