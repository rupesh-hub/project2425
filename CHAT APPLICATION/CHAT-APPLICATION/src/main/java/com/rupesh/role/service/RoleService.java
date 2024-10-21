package com.rupesh.role.service;

import com.rupesh.exception.AuthorizationException;
import com.rupesh.role.entity.Role;
import com.rupesh.role.repository.RoleRepository;
import com.rupesh.shared.GlobalResponse;
import com.rupesh.shared.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository repository;

    @Override
    public GlobalResponse<Void> save(Set<String> request) {

        repository.saveAll(
                request.stream()
                        .map(roleName -> {
                            Role role = new Role();
                            role.setRole(roleName);
                            return role;
                        })
                        .collect(Collectors.toSet())
        );

        return GlobalResponse.success();
    }

    @Override
    public GlobalResponse<Set<String>> getAll(int page, int size) {
        Page<Role> rolePage = repository.findAll(PageRequest.of(page, size));

        Set<String> roles = rolePage.getContent()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());

        return GlobalResponse.success(
                roles,
                Paging.builder()
                        .page(page)
                        .size(size)
                        .totalElement(rolePage.getTotalElements())
                        .totalPage(rolePage.getTotalPages())
                        .first(rolePage.isFirst())
                        .last(rolePage.isLast())
                        .build()
        );
    }

    @Override
    public GlobalResponse<Set<String>> getByName(String role) {
        return repository
                .findByRole(role)
                .map(Role::getRole)
                .map(Set::of)
                .map(GlobalResponse::success)
                .orElseThrow(() -> new AuthorizationException(format("Role by %s not found", role)));

    }

}