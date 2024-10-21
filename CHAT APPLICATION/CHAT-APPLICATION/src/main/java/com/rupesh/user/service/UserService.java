package com.rupesh.user.service;

import com.rupesh.shared.GlobalResponse;
import com.rupesh.shared.Paging;
import com.rupesh.user.entity.User;
import com.rupesh.user.model.UserResponse;
import com.rupesh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public GlobalResponse<Set<UserResponse>> search(int page, int limit, String query) {
        Page<User> searchResults = userRepository.search(PageRequest.of(page, limit), query);

        Set<UserResponse> response = searchResults
                .getContent()
                .stream()
                .map(user -> UserResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build()
                )
                .collect(Collectors.toSet());

        var pageInfo = Paging.builder()
                .first(searchResults.isFirst())
                .last(searchResults.isLast())
                .totalElement(searchResults.getTotalElements())
                .page(searchResults.getNumber())
                .size(searchResults.getSize())
                .totalPage(searchResults.getTotalPages())
                .build();

        return GlobalResponse.success(response, pageInfo);
    }

    public void disconnect(User user){
        var storedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        storedUser.setOnline(false);
        userRepository.save(storedUser);
    }

}
