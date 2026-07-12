package dev.gustavo.math.service.auth;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    public User register(User user) {
        return userService.create(user);
    }

    public AuthenticationTokens login(User user) {
        var existingUser = userService.findByUsername(user.getUsername());

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword()))
            throw new InvalidLoginException();

        return issueTokens(existingUser);
    }

    public AuthenticationTokens refresh(String refreshToken) {
        RefreshTokenResult refreshTokenResult = refreshTokenService.rotate(refreshToken);
        return new AuthenticationTokens(
                accessTokenService.generate(refreshTokenResult.user()),
                refreshTokenResult.refreshToken(),
                "Bearer",
                accessTokenService.getExpiresInSeconds());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private AuthenticationTokens issueTokens(User user) {
        return new AuthenticationTokens(
                accessTokenService.generate(user),
                refreshTokenService.create(user),
                "Bearer",
                accessTokenService.getExpiresInSeconds());
    }
}
