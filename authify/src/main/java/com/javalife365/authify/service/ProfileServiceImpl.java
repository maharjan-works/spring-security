package com.javalife365.authify.service;


import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;
import com.javalife365.authify.mapper.ProfileMapper;
import com.javalife365.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = ProfileMapper.mapToUserEntity(request);
        UserEntity savedProfile = userRepository.save(newProfile);
        return ProfileMapper.mapToProfileResponse(savedProfile);
    }
}
