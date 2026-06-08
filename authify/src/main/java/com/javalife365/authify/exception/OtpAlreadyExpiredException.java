package com.javalife365.authify.exception;

public class OtpAlreadyExpiredException extends RuntimeException {
    public OtpAlreadyExpiredException(String message) {
        super(message);
    }
}
