package com.rupesh.user.resource;

import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import com.rupesh.user.model.AuthenticationRequest;
import com.rupesh.user.model.RegistrationRequest;
import com.rupesh.user.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/authentication")
@RequiredArgsConstructor
public class AuthenticationResource {

    private final IAuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<GlobalResponse<?>> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<GlobalResponse<?>> register(
            @RequestBody @Valid RegistrationRequest request
    ) {
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GlobalResponse<?>> forgotPassword(
            @RequestParam(name = "email") String email
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Password reset email sent successfully."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GlobalResponse<?>> resetPassword(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "token") String token,
            @RequestParam(name = "newPassword") String newPassword
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Password reset successful."));
    }

    @PostMapping(path = "/change-password")
    public ResponseEntity<GlobalResponse<?>> changePassword(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Password updated successfully."));
    }

    @PutMapping("/profile")
    public ResponseEntity<GlobalResponse<?>> updateProfile(
            //@RequestBody @Valid UserProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Profile updated successfully."));
    }

    @GetMapping("/logout")
    public ResponseEntity<GlobalResponse<?>> logout(Authentication authentication) {
        authenticationService.logout((User)authentication.getPrincipal());
        return ResponseEntity.ok(GlobalResponse.success("Logged out successfully."));
    }

    @PutMapping("/assign-role")
    public ResponseEntity<GlobalResponse<?>> assignRole(
            @RequestParam("roles") String[] roles,
            @RequestParam("email") String email,
            @RequestParam("userId") String userId
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Logged out successfully."));
    }

    @PutMapping("/un-assign-role")
    public ResponseEntity<GlobalResponse<?>> unassignRole(
            @RequestParam("role") String role,
            @RequestParam("email") String email,
            @RequestParam("userId") String userId
    ) {
        return ResponseEntity.ok(GlobalResponse.success("Logged out successfully."));
    }

}