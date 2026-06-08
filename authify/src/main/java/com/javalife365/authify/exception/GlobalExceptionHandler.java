package com.javalife365.authify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.Timestamp;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest webRequest) {
        var errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .requestedUrl(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(409).body(errorMessage);
    }

    @ExceptionHandler(EmailSendFailException.class)
    public ResponseEntity<ErrorMessage> handleEmailSendFailException(EmailSendFailException ex, WebRequest webRequest) {
     var errorMessage = ErrorMessage.builder()
             .message(ex.getMessage())
             .status(HttpStatus.SERVICE_UNAVAILABLE.value())
             .timestamp(new Timestamp(System.currentTimeMillis()))
             .requestedUrl(webRequest.getDescription(false))
             .build();
     return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorMessage);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorMessage> handleInvalidOtpException(InvalidOtpException ex, WebRequest webRequest){
        var errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .requestedUrl(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }


    @ExceptionHandler(OtpAlreadyExpiredException.class)
    public ResponseEntity<ErrorMessage> handleOtpAlreadyExpiredException(OtpAlreadyExpiredException ex, WebRequest webRequest){
        var msg = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .requestedUrl(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
    }

}
