package com.javalife365.authify.service;


import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.exception.EmailAlreadyExistsException;
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
        if (!userRepository.existsByEmail(request.getEmail())){
            UserEntity newProfile = ProfileMapper.mapToUserEntity(request);
            newProfile = userRepository.save(newProfile);
            return ProfileMapper.mapToProfileResponse(newProfile);
        }
        throw new EmailAlreadyExistsException("Email Already Exists");

    }
}
