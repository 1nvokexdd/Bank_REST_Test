package com.example.bankcards.exception;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.log4j.Log4j2;



@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        
        log.warn("Validation errors: {}", errorMessages);
        
        GlobalErrorResponse error = new GlobalErrorResponse(
        "VALIDATION_FAILED", 
    "Ошибка валидации данных",
            errorMessages,
            Instant.now()
        );
       
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}