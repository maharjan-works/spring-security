package com.javalife365.authify.exception;

public class EmailSendFailException extends RuntimeException{

    public EmailSendFailException(String message){
        super(message);
    }
}
