package com.javalife365.authify.controller;

import com.javalife365.authify.io.AuthRequest;
import com.javalife365.authify.io.AuthResponse;
import com.javalife365.authify.io.ResetPasswordRequest;
import com.javalife365.authify.service.AppUserDetailsService;
import com.javalife365.authify.service.ProfileService;
import com.javalife365.authify.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtils jwtUtils;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        log.info("credential provided - email: {} and password: {}", request.getEmail(), request.getPassword());
        try{

            //authentication
            authenticate(request.getEmail(), request.getPassword());
            log.info("authentication success with email: {} and password: {}",request.getEmail(), request.getPassword());

            //checking if email exists or not
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());

            //todo : create JWT token and place JWT to cookie
            final String jwtToken = jwtUtils.generateToken(userDetails);
            final Date issuedAt= jwtUtils.extractIssuedAt(jwtToken);
            final Date expiresAt = jwtUtils.extractExpiration(jwtToken);

            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.between(issuedAt.toInstant(), expiresAt.toInstant()))
                    .sameSite("Strict")
                    .build();

            log.info("JWT token : {} is generated", jwtToken);
            log.info("Issued At: {}", issuedAt.toString());
            log.info("Expires At: {}", expiresAt);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken,issuedAt,expiresAt));

        }catch(BadCredentialsException ex ){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Invalid email or password");
            log.info("BadCredentialsException occurred: {}",error.get("message"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch(DisabledException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Account is disabled");
            log.info("DisabledException occurred: {}", error.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }catch(Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            if(ex.getMessage().contains("not found")){
                log.info("email: {} not found in db", request.getEmail());
                error.put("message","Email not found, please register");
            }else{
                error.put("message", ex.getMessage());
            }
            log.info("Authentication Failed: {}", ex.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

    }

    private void authenticate(String email, String password) {
            Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }


    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        log.info("Authenticated email: {}", email);
        return ResponseEntity.ok(email != null);
    }


    @PostMapping("/password-reset-otp")
    public void sendPasswordResetOtp(@RequestParam String email){
        try{
            profileService.sendPasswordResetOtp(email);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        try{
            profileService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/send-email-verification-otp")
    public void sendEmailVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try {
            profileService.sendEmailVerificationOtp(email);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

    }

    @PostMapping("/verify-email-verification-otp")
    public void verifyEmailVerificationOtp(@RequestBody Map<String, Object> request,
                                           @CurrentSecurityContext(expression = "authentication?.name") String email){
        if (request.get("otp").toString().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing OTP");
        }
        try{
            profileService.verifyEmailVerificationOtp(email, request.get("otp").toString());
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout Successfully");

    }






}
