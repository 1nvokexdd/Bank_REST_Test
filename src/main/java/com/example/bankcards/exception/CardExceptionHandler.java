
package com.example.bankcards.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.bankcards.exception.CardException.CardBlockException;
import com.example.bankcards.exception.CardException.CardCreateException;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.CardException.CardRequestBlockException;
import com.example.bankcards.exception.CardException.CardTransferMoneyException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestControllerAdvice
public class CardExceptionHandler {

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> cardNotFoundExceptionHandler(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CARD_NOT_FOUND", 
            ex.getMessage(),
            Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


  


        @ExceptionHandler(CardTransferMoneyException.class)
    public ResponseEntity<ErrorResponse> cardTransferMoneyExceptionHandler(CardTransferMoneyException ex) {
        log.warn("Card transfer exception : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CARD_TRANSFER_FAILED", 
            ex.getMessage(),
            Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }



    @ExceptionHandler(CardBlockException.class)
    public ResponseEntity<ErrorResponse>   cardBlockExceptionHandler(CardBlockException ex) {
        log.warn("Error  : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CARD_BLOCK_FAILED", 
            ex.getMessage(),
            Instant.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }


       @ExceptionHandler(CardCreateException.class)
    public ResponseEntity<ErrorResponse> cardCreateExceptionHandler(CardCreateException ex) {
        log.warn("Error  : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CARD_CREATE_FAILED", 
            ex.getMessage(),
            Instant.now()
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

   


        @ExceptionHandler(CardRequestBlockException.class)
    public ResponseEntity<ErrorResponse> cardRequestBlockException(CardRequestBlockException
 ex) {
        log.warn("Error  : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CARD_BLOCK_REQUEST_FAILED", 
            ex.getMessage(),
            Instant.now()
        );
         return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
 }
}