package com.rupesh.user.mapper;

import com.rupesh.user.entity.User;
import com.rupesh.user.model.UserResponse;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return  UserResponse
                .builder()
                .accountLocked(user.isAccountLocked())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .createdDate(user.getCreatedOn())
                .createdBy(user.getCreatedBy())
                .modifiedDate(user.getModifiedOn())
                .modifiedBy(user.getModifiedBy())
                .lastLoginDate(user.getLastLogin())
                .lastLogoutDate(user.getLastLogout())
                .build();
    }
}