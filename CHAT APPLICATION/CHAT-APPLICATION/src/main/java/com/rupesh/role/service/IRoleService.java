package com.rupesh.role.service;

import com.rupesh.shared.GlobalResponse;

import java.util.Set;

public interface IRoleService {

    GlobalResponse<Void> save(Set<String> request);
    GlobalResponse<Set<String>> getAll(int page, int size);
    GlobalResponse<Set<String>> getByName(String role);

}

