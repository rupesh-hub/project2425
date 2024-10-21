package com.rupesh.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
public class AuthenticationRequest {

    @NotEmpty(message="Email/Username is required.")
    @NotBlank(message="Email/Username is required.")
    private String username;

    @NotEmpty(message="Password is required.")
    @NotBlank(message="Password is required.")
    private String password;

}