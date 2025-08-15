package dev.gustavo.math.exception;

public class TokenDecodingException extends RuntimeException {
    public TokenDecodingException(String message) {
        super("Error with JWT: " + message);
    }
}
