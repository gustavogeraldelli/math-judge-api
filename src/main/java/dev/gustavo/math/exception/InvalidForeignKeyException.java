package dev.gustavo.math.exception;

public class InvalidForeignKeyException extends RuntimeException {
    public InvalidForeignKeyException(String entity, String id) {
        super("Invalid " + entity + " with id " + id);
    }
}
