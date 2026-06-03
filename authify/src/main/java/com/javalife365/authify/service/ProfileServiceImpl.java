package com.javalife365.authify.service;


import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.exception.EmailAlreadyExistsException;
import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;
import com.javalife365.authify.mapper.ProfileMapper;
import com.javalife365.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        if (!userRepository.existsByEmail(request.getEmail())){
            UserEntity newProfile = this.convertToUserEntity(request);
            newProfile = userRepository.save(newProfile);
            emailService.sendWelcomeEmail(request.getEmail(), request.getFirstName() + " " + request.getLastName());
            return convertToProfileResponse(newProfile);
        }
        throw new EmailAlreadyExistsException("Email Already Exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
       UserEntity  existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));
       return  convertToProfileResponse(existingUser);
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
                .resetOtp(null)
                .resetOtpExpireAt(0L)
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
