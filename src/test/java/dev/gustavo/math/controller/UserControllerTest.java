package dev.gustavo.math.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.controller.dto.user.UserUpdateRequestDTO;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.UserService;
import dev.gustavo.math.service.auth.AccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should allow admin to list users")
        void shouldAllowAdminToListUsers() throws Exception {
            UUID adminId = UUID.randomUUID();
            var user = user(UUID.randomUUID(), "john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(userService.findAll(PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1));
            when(userMapper.toUserResponseDTO(user)).thenReturn(response);

            mockMvc.perform(get("/api/v1/users")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].username").value("john"))
                    .andExpect(jsonPath("$.items[0].nickname").value("Johnny"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(userService).findAll(PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should allow user to find own profile")
        void shouldAllowUserToFindOwnProfile() throws Exception {
            UUID userId = UUID.randomUUID();
            var user = user(userId, "john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            authenticate("user-token", userId, "ROLE_USER");
            when(userService.findById(userId)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(response);

            mockMvc.perform(get("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john"))
                    .andExpect(jsonPath("$.nickname").value("Johnny"));

            verify(userService).findById(userId);
        }

        @Test
        @DisplayName("Should allow user to update own profile")
        void shouldAllowUserToUpdateOwnProfile() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new UserUpdateRequestDTO("johnny", "newPassword", "Johnny");
            var userUpdate = user(null, "johnny", "Johnny");
            var updatedUser = user(userId, "johnny", "Johnny");
            var response = new UserResponseDTO("johnny", "Johnny");

            authenticate("user-token", userId, "ROLE_USER");
            when(userMapper.toUser(any(UserUpdateRequestDTO.class))).thenReturn(userUpdate);
            when(userService.update(userId, userUpdate)).thenReturn(updatedUser);
            when(userMapper.toUserResponseDTO(updatedUser)).thenReturn(response);

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("johnny"))
                    .andExpect(jsonPath("$.nickname").value("Johnny"));

            verify(userService).update(userId, userUpdate);
        }

        @Test
        @DisplayName("Should allow user to delete own profile")
        void shouldAllowUserToDeleteOwnProfile() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(delete("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isNoContent());

            verify(userService).delete(userId);
        }

    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should reject users list for regular user")
        void shouldRejectUsersListForRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(get("/api/v1/users")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).findAll(any());
        }

        @Test
        @DisplayName("Should reject another user's profile access")
        void shouldRejectAnotherUserProfileAccess() throws Exception {
            UUID userId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(get("/api/v1/users/{id}", anotherUserId)
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).findById(any());
        }

        @Test
        @DisplayName("Should allow admin to access any user")
        void shouldAllowAdminToAccessAnyUser() throws Exception {
            UUID adminId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();
            var user = user(anotherUserId, "john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(userService.findById(anotherUserId)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(response);

            mockMvc.perform(get("/api/v1/users/{id}", anotherUserId)
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john"));

            verify(userService).findById(anotherUserId);
        }

        @Test
        @DisplayName("Should reject another user's profile update")
        void shouldRejectAnotherUserProfileUpdate() throws Exception {
            UUID userId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();
            var request = new UserUpdateRequestDTO("johnny", null, null);

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(put("/api/v1/users/{id}", anotherUserId)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(userService, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should reject another user's profile deletion")
        void shouldRejectAnotherUserProfileDeletion() throws Exception {
            UUID userId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(delete("/api/v1/users/{id}", anotherUserId)
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).delete(any());
        }

        @Test
        @DisplayName("Should allow admin to delete any user")
        void shouldAllowAdminToDeleteAnyUser() throws Exception {
            UUID adminId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(delete("/api/v1/users/{id}", anotherUserId)
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isNoContent());

            verify(userService).delete(anotherUserId);
        }

        @Test
        @DisplayName("Should return unauthorized when access token is missing")
        void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid access token")
        void shouldReturnUnauthorizedForInvalidAccessToken() throws Exception {
            when(accessTokenService.validate("invalid-token"))
                    .thenThrow(new TokenDecodingException("failed to decode"));

            mockMvc.perform(get("/api/v1/users")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject update with empty username")
        void shouldRejectUpdateWithEmptyUsername() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new UserUpdateRequestDTO("", null, null);

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").value("Username must have between 4 and 64 characters"));

            verify(userService, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should reject update with empty password")
        void shouldRejectUpdateWithEmptyPassword() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new UserUpdateRequestDTO(null, "", null);

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").value("Password must have at least 8 characters"));

            verify(userService, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should allow update with all optional fields absent")
        void shouldAllowUpdateWithAllOptionalFieldsAbsent() throws Exception {
            UUID userId = UUID.randomUUID();
            var userUpdate = new User();
            var updatedUser = user(userId, "john", "Johnny");
            var response = new UserResponseDTO("john", "Johnny");

            authenticate("user-token", userId, "ROLE_USER");
            when(userMapper.toUser(any(UserUpdateRequestDTO.class))).thenReturn(userUpdate);
            when(userService.update(userId, userUpdate)).thenReturn(updatedUser);
            when(userMapper.toUserResponseDTO(updatedUser)).thenReturn(response);

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john"));

            verify(userService).update(userId, userUpdate);
        }
    }

    private User user(UUID id, String username, String nickname) {
        var user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setNickname(nickname);
        return user;
    }

    private void authenticate(String token, UUID userId, String role) {
        when(accessTokenService.validate(token)).thenReturn(true);
        when(accessTokenService.getUserId(token)).thenReturn(userId);
        when(accessTokenService.getUserRole(token)).thenReturn(role);
    }
}
