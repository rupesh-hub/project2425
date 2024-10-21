package com.rupesh.user.service;

import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import com.rupesh.user.model.AuthenticationRequest;
import com.rupesh.user.model.AuthenticationResponse;
import com.rupesh.user.model.RegistrationRequest;

public interface IAuthenticationService {
    void register(RegistrationRequest request);

    GlobalResponse<AuthenticationResponse> authenticate(AuthenticationRequest request);

    GlobalResponse<Void> logout(User principal);
}