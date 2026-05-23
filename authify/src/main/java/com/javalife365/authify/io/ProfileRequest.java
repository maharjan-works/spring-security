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

    @NotBlank (message = "Firstname is required")
    private String firstName;

    @NotBlank (message = "Lastname is required")
    private String lastName;

    @Email (message = "Email should be valid")
    @NotNull (message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=5, message = "Password should be atleast 5 characters")
    private String password;
}
