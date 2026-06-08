package com.javalife365.authify.service;


import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.exception.EmailAlreadyExistsException;
import com.javalife365.authify.exception.InvalidOtpException;
import com.javalife365.authify.exception.OtpAlreadyExpiredException;
import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;
import com.javalife365.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        if (!userRepository.existsByEmail(request.getEmail())) {
            UserEntity newProfile = this.convertToUserEntity(request);
            newProfile = userRepository.save(newProfile);
            emailService.sendWelcomeEmail(request.getEmail(), request.getFirstName() + " " + request.getLastName());
            return convertToProfileResponse(newProfile);
        }
        throw new EmailAlreadyExistsException("Email Already Exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendPasswordResetOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " has not created account before."));

        //todo: generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        log.info("6 digits otp generated: {}", otp);

        //todo: calculate expiry time (current time + 15mins )
        long expiryTime = System.currentTimeMillis() + (1000 * 60 * 5);

        //todo: update user entity
        existingUser.setPasswordResetOtp(otp);
        existingUser.setPasswordResetOtpExpireAt(expiryTime);

        //todo: save to db
        userRepository.save(existingUser);
        log.info("user entity saved with otp in db");

        //todo: send email with otp
        log.info("Sending email to {} with otp: {}", existingUser.getEmail(), otp);
        emailService.sendPasswordResetOtpEmail(existingUser.getEmail(), otp);
        log.info("SUCCESS on sending email to {} with otp: {}", existingUser.getEmail(), otp);

    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        log.info("resetting existing password for {} with new password: {}", email,passwordEncoder.encode(newPassword));
        // step 1: check if email existed
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email: "+ email + " not existed"));
        // step 2: check if OTP is valid
        if (existingUser.getPasswordResetOtp() == null || !existingUser.getPasswordResetOtp().equals(otp)){
            throw new InvalidOtpException("Invalid OTP");
        }
        // step 3: check if OTP is not expired
        if (existingUser.getPasswordResetOtpExpireAt() < System.currentTimeMillis()){
            throw new OtpAlreadyExpiredException("OTP expired");
        }
        // step 4: update new password, set restOtp = null, set expiresAt = 0 and save to db
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setPasswordResetOtp(null);
        existingUser.setPasswordResetOtpExpireAt(0L);
        userRepository.save(existingUser);
        log.info("new password: {} has been updated for {} in db successfully.", passwordEncoder.encode(newPassword), email);
        //todo: send email about password has been updated successfully
        emailService.sendEmailAfterPasswordUpdated(existingUser.getEmail());
    }


    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .passwordResetOtp(null)
                .passwordResetOtpExpireAt(0L)
                .build();
    }

    private ProfileResponse convertToProfileResponse(UserEntity user) {
        return ProfileResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isAccountVerified(user.getIsAccountVerified())
                .build();
    }
}
