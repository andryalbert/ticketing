package com.demo.ticketing.utils.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ResponseRestControllerException {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> inputValidationException(ConstraintViolationException exception) {
        Map<String, String> errorMessages = new HashMap<>();
        exception.getConstraintViolations().forEach(constraint ->
                    errorMessages.put(String.valueOf(constraint.getPropertyPath()), constraint.getMessageTemplate()));

        ErrorResponse response = ErrorResponse.builder()
                .statusCode(400)
                .errorType(ErrorType.INPUT_MISMATCH)
                .details(errorMessages)
                .suggestion("Assurez vous que les données pour sont conformes")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException exception) {
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("error", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .statusCode(400)
                .errorType(ErrorType.INPUT_MISMATCH)
                .details(errorMessage)
                .suggestion("Assurez vous que les données sont correctes")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernameNotFoundException(UsernameNotFoundException exception) {
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("error", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(401)
                .errorType(ErrorType.NOT_AUTHORIZED)
                .details(errorMessage)
                .suggestion("Les informations de connexion sont incorrectes")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException exception) {
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("error", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(404)
                .errorType(ErrorType.RESOURCE_NOT_FOUND)
                .details(errorMessage)
                .suggestion("Les informations que vous entrez, ne sont pas trouvées")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


}
