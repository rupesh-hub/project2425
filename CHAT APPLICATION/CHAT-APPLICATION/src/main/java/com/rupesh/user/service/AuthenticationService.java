package com.rupesh.user.service;

import com.rupesh.role.repository.RoleRepository;
import com.rupesh.security.TokenService;
import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import com.rupesh.user.model.AuthenticationRequest;
import com.rupesh.user.model.AuthenticationResponse;
import com.rupesh.user.model.RegistrationRequest;
import com.rupesh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegistrationRequest request) {
        var role = roleRepository.findByRole("USER")
                .map(List::of)
                .orElseThrow(() -> new IllegalStateException("Could not find role 'USER'"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(role)
                .build();

        userRepository.save(user);
    }

    @Override
    public GlobalResponse<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            Map<String, Object> claims = new HashMap<>();
            var user = (User) auth.getPrincipal();
            System.out.println(user);
            claims.put("name", user.fullName());
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());
            claims.put("authorities", user.getAuthorities());

            var token = TokenService.generateToken(claims, user);

            //update user login date and status
            user.setLastLogin(LocalDateTime.now());
            user.setOnline(true);
            userRepository.save(user);

            return GlobalResponse.success(
                    AuthenticationResponse
                            .builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage().toUpperCase());
        }


    }

    @Override
    public GlobalResponse<Void> logout(User principal) {
        principal.setLastLogout(LocalDateTime.now());
        userRepository.save(principal);
        return GlobalResponse.success();
    }

}