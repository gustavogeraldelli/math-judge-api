package dev.gustavo.math.service.auth;

import dev.gustavo.math.entity.RefreshToken;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.InvalidRefreshTokenException;
import dev.gustavo.math.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${api.jwt.refresh-token.expiration-seconds:604800}")
    private long refreshTokenExpirationSeconds;

    @Transactional
    public String create(User user) {
        String rawRefreshToken = generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(rawRefreshToken));
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds));

        refreshTokenRepository.save(refreshToken);
        return rawRefreshToken;
    }

    @Transactional
    public RefreshTokenResult rotate(String rawRefreshToken) {
        RefreshToken currentToken = findValidToken(rawRefreshToken);
        currentToken.setRevokedAt(Instant.now());
        String nextToken = create(currentToken.getUser());
        return new RefreshTokenResult(currentToken.getUser(), nextToken);
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        RefreshToken refreshToken = findValidToken(rawRefreshToken);
        refreshToken.setRevokedAt(Instant.now());
    }

    String hash(String rawRefreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedToken = digest.digest(rawRefreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedToken);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }

    private RefreshToken findValidToken(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isRevoked() || refreshToken.isExpired())
            throw new InvalidRefreshTokenException();

        return refreshToken;
    }

    private String generateRefreshToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
