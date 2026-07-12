package dev.gustavo.math.service.auth;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.exception.UsernameIsAlreadyInUseException;
import dev.gustavo.math.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
    }

    @Nested
    @DisplayName("User Registration")
    class RegisterUser {
        @Test
        @DisplayName("Should register a user successfully")
        void registerShouldRegisterAndReturnUserWhenSuccessful() {
            when(userService.create(any(User.class))).thenReturn(user);

            var registeredUser = authenticationService.register(user);

            assertNotNull(registeredUser);
            assertEquals(user.getUsername(), registeredUser.getUsername());
            verify(userService, times(1)).create(user);
        }

        @Test
        @DisplayName("should throw exception when username already exists")
        void registerShouldThrowExceptionWhenUsernameExists() {
            when(userService.create(any(User.class)))
                    .thenThrow(new UsernameIsAlreadyInUseException("testuser"));

            assertThrows(UsernameIsAlreadyInUseException.class, () -> authenticationService.register(user));

            verify(userService, times(1)).create(user);
        }
    }

    @Nested
    @DisplayName("User Login")
    class LoginUser {
        @Test
        @DisplayName("Should login successfully and return authentication tokens")
        void loginShouldReturnTokensWhenLoginIsSuccessful() {
            User existingUser = new User();
            existingUser.setUsername("username");
            existingUser.setPassword("encodedPassword");

            when(userService.findByUsername(user.getUsername())).thenReturn(existingUser);
            when(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())).thenReturn(true);
            when(accessTokenService.generate(existingUser)).thenReturn("access-token");
            when(accessTokenService.getExpiresInSeconds()).thenReturn(1800L);
            when(refreshTokenService.create(existingUser)).thenReturn("refresh-token");

            AuthenticationTokens tokens = authenticationService.login(user);

            assertNotNull(tokens);
            assertEquals("access-token", tokens.accessToken());
            assertEquals("refresh-token", tokens.refreshToken());
            assertEquals("Bearer", tokens.tokenType());
            assertEquals(1800L, tokens.expiresIn());
            verify(userService, times(1)).findByUsername(user.getUsername());
            verify(passwordEncoder, times(1)).matches(user.getPassword(), existingUser.getPassword());
            verify(accessTokenService, times(1)).generate(existingUser);
            verify(refreshTokenService, times(1)).create(existingUser);
        }

        @Test
        @DisplayName("Should throw exception when user is not found")
        void loginShouldThrowExceptionWhenUserNotFound() {
            when(userService.findByUsername(user.getUsername()))
                    .thenThrow(new EntityNotFoundException("User", user.getUsername()));

            assertThrows(EntityNotFoundException.class, () -> authenticationService.login(user));

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(accessTokenService, never()).generate(any(User.class));
            verify(refreshTokenService, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for invalid password")
        void loginShouldThrowExceptionForInvalidPassword() {
            User existingUser = new User();
            existingUser.setUsername("username");
            existingUser.setPassword("encodedPassword");

            when(userService.findByUsername(user.getUsername())).thenReturn(existingUser);
            when(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())).thenReturn(false);

            assertThrows(InvalidLoginException.class, () -> authenticationService.login(user));

            verify(accessTokenService, never()).generate(any(User.class));
            verify(refreshTokenService, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Should refresh tokens when refresh token is valid")
        void refreshShouldReturnNewTokensWhenRefreshTokenIsValid() {
            User existingUser = new User();
            when(refreshTokenService.rotate("refresh-token"))
                    .thenReturn(new RefreshTokenResult(existingUser, "new-refresh-token"));
            when(accessTokenService.generate(existingUser)).thenReturn("new-access-token");
            when(accessTokenService.getExpiresInSeconds()).thenReturn(1800L);

            AuthenticationTokens tokens = authenticationService.refresh("refresh-token");

            assertEquals("new-access-token", tokens.accessToken());
            assertEquals("new-refresh-token", tokens.refreshToken());
            assertEquals("Bearer", tokens.tokenType());
            assertEquals(1800L, tokens.expiresIn());
            verify(refreshTokenService).rotate("refresh-token");
            verify(accessTokenService).generate(existingUser);
        }

        @Test
        @DisplayName("Should revoke refresh token on logout")
        void logoutShouldRevokeRefreshToken() {
            authenticationService.logout("refresh-token");

            verify(refreshTokenService).revoke("refresh-token");
        }
    }

}
