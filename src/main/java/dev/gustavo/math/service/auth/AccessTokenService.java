package dev.gustavo.math.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.TokenDecodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AccessTokenService {

    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 1800;

    @Value("${api.jwt.secret}")
    String secret;

    public String generate(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRATION_SECONDS))
                .withIssuer("mathjudge")
                .sign(algorithm);
    }

    public boolean validate(String accessToken) {
        decode(accessToken);
        return true;
    }

    public UUID getUserId(String accessToken) {
        try  {
            DecodedJWT jwt = decode(accessToken);
            return UUID.fromString(jwt.getSubject());
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to extract user id");
        }
    }

    public String getUserRole(String accessToken) {
        try  {
            DecodedJWT jwt = decode(accessToken);
            return jwt.getClaim("role").asString();
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to extract role");
        }
    }

    public long getExpiresInSeconds() {
        return ACCESS_TOKEN_EXPIRATION_SECONDS;
    }

    private DecodedJWT decode(String accessToken) {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(accessToken);
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to decode");
        }
    }

}
