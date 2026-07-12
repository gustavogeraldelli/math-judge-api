package dev.gustavo.math.service.auth;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.UserRole;
import dev.gustavo.math.exception.TokenDecodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenServiceTest {

    private AccessTokenService accessTokenService;
    private User user;

    @BeforeEach
    void setUp() {
        accessTokenService = new AccessTokenService();
        ReflectionTestUtils.setField(accessTokenService, "secret", "test-secret");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setRole(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("Should generate and validate access token")
    void validateShouldReturnTrueWhenAccessTokenIsValid() {
        String accessToken = accessTokenService.generate(user);

        assertTrue(accessTokenService.validate(accessToken));
        assertEquals(user.getId(), accessTokenService.getUserId(accessToken));
        assertEquals(UserRole.ROLE_USER.name(), accessTokenService.getUserRole(accessToken));
    }

    @Test
    @DisplayName("Should reject altered access token")
    void validateShouldRejectAlteredAccessToken() {
        String accessToken = accessTokenService.generate(user);
        String alteredAccessToken = accessToken.substring(0, accessToken.length() - 2) + "xx";

        assertThrows(TokenDecodingException.class, () -> accessTokenService.validate(alteredAccessToken));
        assertThrows(TokenDecodingException.class, () -> accessTokenService.getUserId(alteredAccessToken));
    }
}
