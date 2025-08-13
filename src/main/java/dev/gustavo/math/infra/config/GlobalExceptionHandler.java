package dev.gustavo.math.infra.config;

import dev.gustavo.math.exception.UsernameIsAlreadyInUseException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameIsAlreadyInUseException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameIsAlreadyInUseException(UsernameIsAlreadyInUseException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
    }

}
