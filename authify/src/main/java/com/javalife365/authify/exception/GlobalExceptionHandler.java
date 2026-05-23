package com.javalife365.authify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.Timestamp;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest webRequest){
        var errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .requestedUrl(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(409).body(errorMessage);
    }

}
