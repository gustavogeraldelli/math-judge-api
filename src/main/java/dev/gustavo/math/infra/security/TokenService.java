package dev.gustavo.math.infra.security;

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
public class TokenService {

    @Value("${api.jwt.secret}")
    String secret;

    public String generate(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(Instant.now())
                //.withExpiresAt()
                .withIssuer("mathjudge")
                .sign(algorithm);
    }

    public boolean validate(String token) {
        decode(token);
        return true;
    }

    public UUID getUserId(String token) {
        try  {
            DecodedJWT jwt = JWT.decode(token);
            return UUID.fromString(jwt.getSubject());
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to extract user id");
        }
    }

    public String getUserRole(String token) {
        try  {
            DecodedJWT jwt = decode(token);
            return jwt.getClaim("role").asString();
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to extract role");
        }
    }

    private DecodedJWT decode(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        }
        catch (Exception e) {
            throw new TokenDecodingException("failed to decode");
        }
    }

//    public Instant generateExpiresAt() {
//        return Instant.now().plusSeconds(3600);
//    }

}
