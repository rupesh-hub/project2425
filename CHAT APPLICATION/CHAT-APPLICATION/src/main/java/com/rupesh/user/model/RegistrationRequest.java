package com.rupesh.user.model;


import com.rupesh.shared.validators.ValidPassword;
import com.rupesh.shared.validators.ValidUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotEmpty(message="Firstname is required.")
    @NotBlank(message="Firstname is required.")
    private String firstName;

    @NotEmpty(message="Lastname is required.")
    @NotBlank(message="Lastname is required.")
    private String lastName;

    @ValidUsername(message = "Invalid username.")
    private String username;

    @NotEmpty(message="Email is required.")
    @NotBlank(message="Email is required.")
    @Email(message="Email is invalid.")
    private String email;

    @ValidPassword(message="Invalid password.")
    private String password;

}