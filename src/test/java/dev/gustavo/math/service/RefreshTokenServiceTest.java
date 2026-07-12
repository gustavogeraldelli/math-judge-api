package dev.gustavo.math.service;

import dev.gustavo.math.entity.RefreshToken;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.InvalidRefreshTokenException;
import dev.gustavo.math.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;
    private User user;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationSeconds", 604800L);

        user = new User();
    }

    @Test
    @DisplayName("Should create refresh token and store only hash")
    void createShouldSaveOnlyTokenHash() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String rawRefreshToken = refreshTokenService.create(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken savedToken = captor.getValue();
        assertNotNull(rawRefreshToken);
        assertEquals(user, savedToken.getUser());
        assertNotEquals(rawRefreshToken, savedToken.getTokenHash());
        assertEquals(64, savedToken.getTokenHash().length());
        assertNotNull(savedToken.getExpiresAt());
    }

    @Test
    @DisplayName("Should rotate valid refresh token")
    void rotateShouldRevokeCurrentTokenAndReturnNextToken() {
        RefreshToken currentToken = validRefreshToken();
        String rawToken = "refresh-token";
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(currentToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenResult result = refreshTokenService.rotate(rawToken);

        assertEquals(user, result.user());
        assertNotNull(result.refreshToken());
        assertNotNull(currentToken.getRevokedAt());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should reject revoked refresh token")
    void rotateShouldRejectRevokedRefreshToken() {
        RefreshToken currentToken = validRefreshToken();
        currentToken.setRevokedAt(Instant.now());
        String rawToken = "refresh-token";
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(currentToken));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.rotate(rawToken));
    }

    @Test
    @DisplayName("Should reject expired refresh token")
    void rotateShouldRejectExpiredRefreshToken() {
        RefreshToken currentToken = validRefreshToken();
        currentToken.setExpiresAt(Instant.now().minusSeconds(1));
        String rawToken = "refresh-token";
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(currentToken));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.rotate(rawToken));
    }

    private RefreshToken validRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(refreshTokenService.hash("refresh-token"));
        refreshToken.setExpiresAt(Instant.now().plusSeconds(60));
        return refreshToken;
    }
}
