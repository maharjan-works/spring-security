package com.javalife365.authify.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isAccountVerified;
}
