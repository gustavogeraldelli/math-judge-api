package dev.gustavo.math.service;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.exception.UsernameIsAlreadyInUseException;
import dev.gustavo.math.infra.security.TokenService;
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
    private TokenService tokenService;

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

            assertThrows(UsernameIsAlreadyInUseException.class, () -> {
                authenticationService.register(user);
            });

            verify(userService, times(1)).create(user);
        }
    }

    @Nested
    @DisplayName("User Login")
    class LoginUser {
        @Test
        @DisplayName("Should login successfully and return a token")
        void loginShouldReturnTokenWhenLoginIsSuccessful() {
            User existingUser = new User();
            existingUser.setUsername("username");
            existingUser.setPassword("encodedPassword");

            when(userService.findByUsername(user.getUsername())).thenReturn(existingUser);
            when(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())).thenReturn(true);
            when(tokenService.generate(existingUser)).thenReturn("jwt-token");

            String token = authenticationService.login(user);

            assertNotNull(token);
            assertEquals("jwt-token", token);
            verify(userService, times(1)).findByUsername(user.getUsername());
            verify(passwordEncoder, times(1)).matches(user.getPassword(), existingUser.getPassword());
            verify(tokenService, times(1)).generate(existingUser);
        }

        @Test
        @DisplayName("Should throw exception when user is not found")
        void loginShouldThrowExceptionWhenUserNotFound() {
            when(userService.findByUsername(user.getUsername()))
                    .thenThrow(new EntityNotFoundException("User", user.getUsername()));

            assertThrows(EntityNotFoundException.class, () -> {
                authenticationService.login(user);
            });

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(tokenService, never()).generate(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for invalid password")
        void loginShouldThrowExceptionForInvalidPassword() {
            User existingUser = new User();
            existingUser.setUsername("username");
            existingUser.setPassword("encodedPassword");

            when(userService.findByUsername(user.getUsername())).thenReturn(existingUser);
            when(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())).thenReturn(false);

            assertThrows(InvalidLoginException.class, () -> {
                authenticationService.login(user);
            });

            verify(tokenService, never()).generate(any(User.class));
        }
    }

}