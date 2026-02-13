package com.loginsystem.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.loginsystem.dto.MessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle token refresh exceptions
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<MessageResponse> handleTokenRefreshException(TokenRefreshException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new MessageResponse(ex.getMessage()));
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGlobalException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new MessageResponse("An error occurred: " + ex.getMessage()));
    }
}
