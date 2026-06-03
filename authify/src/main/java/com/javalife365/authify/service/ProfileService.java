package com.javalife365.authify.service;

import com.javalife365.authify.io.ProfileRequest;
import com.javalife365.authify.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);
}
