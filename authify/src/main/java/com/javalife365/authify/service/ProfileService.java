package com.javalife365.authify.service;

import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendPasswordResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendEmailVerificationOtp(String email);

    void verifyEmailVerificationOtp(String email, String otp);

}
