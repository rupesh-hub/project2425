package com.rupesh.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean accountLocked;
    private boolean enabled;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLogoutDate;

}