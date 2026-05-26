package com.javalife365.authify.controller;

import com.javalife365.authify.io.AuthRequest;
import com.javalife365.authify.io.AuthResponse;
import com.javalife365.authify.service.AppUserDetailsService;
import com.javalife365.authify.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtils jwtUtils;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){

        try{
            authenticate(request.getEmail(), request.getPassword());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            //todo : create JWT token and place JWT to cookies
            final String jwtToken = jwtUtils.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken));
        }catch(BadCredentialsException ex ){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch(DisabledException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }catch(Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

}
