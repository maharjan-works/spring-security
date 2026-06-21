package com.javalife365.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileRequest {

    @NotBlank (message = "Firstname must not be empty")
    private String firstName;

    @NotBlank (message = "Lastname must not be empty")
    private String lastName;

    @Email (message = "Email is invalid")
    @NotBlank (message = "Email must not be empty")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=5, message = "Password must be at least 5 characters")
    private String password;
}
