package com.javalife365.authify.mapper;

import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;

import java.util.UUID;

public class ProfileMapper {

    public static UserEntity mapToUserEntity(ProfileRequest request){
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtp(null)
                .resetOtpExpireAt(0L)
                .build();
    }

    public static ProfileResponse mapToProfileResponse(UserEntity user) {
        return ProfileResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isAccountVerified(user.getIsAccountVerified())
                .build();
    }
}
