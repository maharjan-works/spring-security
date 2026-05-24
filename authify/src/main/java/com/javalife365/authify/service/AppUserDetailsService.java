package com.javalife365.authify.service;

import com.javalife365.authify.entity.UserEntity;
import com.javalife365.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity existingUser = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email: " + username + "  not found"));
        return new User(existingUser.getEmail(),existingUser.getPassword(), new ArrayList<>());
    }
}
