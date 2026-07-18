package dev.gustavo.math.controller.advice;

import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.ForbiddenOperationException;
import dev.gustavo.math.exception.InvalidLoginException;
import dev.gustavo.math.exception.InvalidProblemVariablesException;
import dev.gustavo.math.exception.InvalidRefreshTokenException;
import dev.gustavo.math.exception.RateLimitExceededException;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.exception.UsernameIsAlreadyInUseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(new ValidationErrorResponseDTO("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenDecodingException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenDecodingException(TokenDecodingException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidLoginException(InvalidLoginException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenOperationException(ForbiddenOperationException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidProblemVariablesException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidProblemVariablesException(InvalidProblemVariablesException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimitExceededException(RateLimitExceededException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Rate-Limit-Limit", Long.toString(e.getLimit()));
        headers.set("X-Rate-Limit-Remaining", "0");
        headers.set(HttpHeaders.RETRY_AFTER, Long.toString(e.getRetryAfterSeconds()));
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), headers, HttpStatus.TOO_MANY_REQUESTS);
    }

}
