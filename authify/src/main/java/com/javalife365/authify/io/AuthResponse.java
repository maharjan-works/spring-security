package com.javalife365.authify.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String email;
    private String token;
    private Date issuedAt;
    private Date expiresAt;
}
