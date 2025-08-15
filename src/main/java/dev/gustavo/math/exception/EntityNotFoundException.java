package dev.gustavo.math.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, String id) {
        super(String.format("%s with id %s not found", entity, id));
    }
}
