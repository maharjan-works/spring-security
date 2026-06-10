package com.javalife365.authify.controller;

import com.javalife365.authify.io.AuthRequest;
import com.javalife365.authify.io.AuthResponse;
import com.javalife365.authify.io.ResetPasswordRequest;
import com.javalife365.authify.service.AppUserDetailsService;
import com.javalife365.authify.service.ProfileService;
import com.javalife365.authify.utils.JwtUtils;
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
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
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
        log.info("credential provided - email: {} and password: {}", request.getEmail(), request.getEmail());
        try{
            authenticate(request.getEmail(), request.getPassword());
            log.info("AUTHENTICATION SUCCESS");
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            //todo : create JWT token and place JWT to cookies
            final String jwtToken = jwtUtils.generateToken(userDetails);
            final Date issuedAt= jwtUtils.extractIssuedAt(jwtToken);
            final Date expiresAt = jwtUtils.extractExpiration(jwtToken);
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken,issuedAt,expiresAt));
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
        log.info("Authenticating credentials with authentication manager");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
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





}
