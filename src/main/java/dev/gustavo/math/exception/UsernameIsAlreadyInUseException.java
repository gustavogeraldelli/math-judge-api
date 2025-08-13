package dev.gustavo.math.exception;

public class UsernameIsAlreadyInUseException extends RuntimeException {
    public UsernameIsAlreadyInUseException(String username) {
        super("Username " + username + " is already in use");
    }
}
