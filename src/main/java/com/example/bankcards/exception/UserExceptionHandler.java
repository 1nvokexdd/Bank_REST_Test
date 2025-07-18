package com.example.bankcards.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.bankcards.exception.UserException.UserAlreadyExistException;
import com.example.bankcards.exception.UserException.UserNotFoundException;
import com.example.bankcards.exception.UserException.UserNotOwnsThisCardException;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestControllerAdvice
public class UserExceptionHandler {
    

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(UserNotFoundException ex) {
        log.warn("User found error : {}", ex.getMessage());
       ErrorResponse error = new ErrorResponse(
            "USER_NOT_FOUND", 
            ex.getMessage(),
            Instant.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(UserNotOwnsThisCardException.class)
    public ResponseEntity<ErrorResponse> UserNotOwnsThisCardExceptionHandler(UserNotOwnsThisCardException ex) {
        log.warn("Error  : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "USER_NOT_OWNS_THIS_CARD", 
            ex.getMessage(),
            Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> UserAlreadyExistExceptionHandler(UserAlreadyExistException ex) {
        log.warn("Error  : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "USER_ALREADY_EXIST", 
            ex.getMessage(),
            Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
