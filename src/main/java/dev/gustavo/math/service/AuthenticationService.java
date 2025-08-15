package dev.gustavo.math.service;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.infra.security.TokenService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public User register(User user) {
        return userService.create(user);
        // maybe return token
    }

    public String login(User user) {
        var existingUser = userService.findByUsername(user.getUsername())
                .orElseThrow(InvalidLoginException::new);

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword()))
            throw new InvalidLoginException();

        return tokenService.generate(existingUser);
    }
}
