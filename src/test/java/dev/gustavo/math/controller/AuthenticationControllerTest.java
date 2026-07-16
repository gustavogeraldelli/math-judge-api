package dev.gustavo.math.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.RefreshTokenRequestDTO;
import dev.gustavo.math.controller.dto.user.UserCreateRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.exception.InvalidRefreshTokenException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.auth.AccessTokenService;
import dev.gustavo.math.service.auth.AuthenticationService;
import dev.gustavo.math.service.auth.AuthenticationTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserMapper userMapper;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should register user")
        void shouldRegisterUser() throws Exception {
            var request = new UserCreateRequestDTO("john", "password123", "Johnny");
            var user = user("john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            when(userMapper.toUser(any(UserCreateRequestDTO.class))).thenReturn(user);
            when(authenticationService.register(user)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(response);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("john"))
                    .andExpect(jsonPath("$.nickname").value("Johnny"));

            verify(authenticationService).register(user);
        }

        @Test
        @DisplayName("Should login user")
        void shouldLoginUser() throws Exception {
            var request = new LoginRequestDTO("john", "password123");
            var user = user("john", null);
            var tokens = new AuthenticationTokens("access-token", "refresh-token", "Bearer", 1800);

            when(userMapper.toUser(any(LoginRequestDTO.class))).thenReturn(user);
            when(authenticationService.login(user)).thenReturn(tokens);

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").value(1800));

            verify(authenticationService).login(user);
        }

        @Test
        @DisplayName("Should refresh tokens")
        void shouldRefreshTokens() throws Exception {
            var request = new RefreshTokenRequestDTO("current-refresh-token");
            var tokens = new AuthenticationTokens("new-access-token", "new-refresh-token", "Bearer", 1800);

            when(authenticationService.refresh("current-refresh-token")).thenReturn(tokens);

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").value(1800));

            verify(authenticationService).refresh("current-refresh-token");
        }

        @Test
        @DisplayName("Should logout user")
        void shouldLogoutUser() throws Exception {
            var request = new RefreshTokenRequestDTO("current-refresh-token");

            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(authenticationService).logout("current-refresh-token");
        }
    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should allow register without access token")
        void shouldAllowRegisterWithoutAccessToken() throws Exception {
            var request = new UserCreateRequestDTO("john", "password123", "Johnny");
            var user = user("john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            when(userMapper.toUser(any(UserCreateRequestDTO.class))).thenReturn(user);
            when(authenticationService.register(user)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(response);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid login")
        void shouldReturnUnauthorizedForInvalidLogin() throws Exception {
            var request = new LoginRequestDTO("john", "wrong-password");
            var user = user("john", null);

            when(userMapper.toUser(any(LoginRequestDTO.class))).thenReturn(user);
            when(authenticationService.login(user))
                    .thenThrow(new InvalidLoginException());

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Invalid username or password"));
        }

        @Test
        @DisplayName("Should return unauthorized for invalid refresh token")
        void shouldReturnUnauthorizedForInvalidRefreshToken() throws Exception {
            var request = new RefreshTokenRequestDTO("invalid-refresh-token");

            when(authenticationService.refresh("invalid-refresh-token"))
                    .thenThrow(new InvalidRefreshTokenException());

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Invalid refresh token"));
        }

        @Test
        @DisplayName("Should return unauthorized for revoked refresh token")
        void shouldReturnUnauthorizedForRevokedRefreshToken() throws Exception {
            var request = new RefreshTokenRequestDTO("revoked-refresh-token");

            when(authenticationService.refresh("revoked-refresh-token"))
                    .thenThrow(new InvalidRefreshTokenException());

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Invalid refresh token"));
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject registration without username")
        void shouldRejectRegistrationWithoutUsername() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "password": "password123",
                                      "nickname": "Johnny"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").value("Username is required"));

            verify(authenticationService, never()).register(any());
        }

        @Test
        @DisplayName("Should reject registration without password")
        void shouldRejectRegistrationWithoutPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "username": "john",
                                      "nickname": "Johnny"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").value("Password is required"));

            verify(authenticationService, never()).register(any());
        }

        @Test
        @DisplayName("Should reject login without username")
        void shouldRejectLoginWithoutUsername() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "password": "password123"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").value("Username is required"));

            verify(authenticationService, never()).login(any());
        }

        @Test
        @DisplayName("Should reject login without password")
        void shouldRejectLoginWithoutPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "username": "john"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").value("Password is required"));

            verify(authenticationService, never()).login(any());
        }

        @Test
        @DisplayName("Should reject refresh without refresh token")
        void shouldRejectRefreshWithoutRefreshToken() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.refreshToken").value("Refresh token is required"));

            verify(authenticationService, never()).refresh(any());
        }

        @Test
        @DisplayName("Should reject logout without refresh token")
        void shouldRejectLogoutWithoutRefreshToken() throws Exception {
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.refreshToken").value("Refresh token is required"));

            verify(authenticationService, never()).logout(any());
        }
    }

    private User user(String username, String nickname) {
        var user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        return user;
    }
}
