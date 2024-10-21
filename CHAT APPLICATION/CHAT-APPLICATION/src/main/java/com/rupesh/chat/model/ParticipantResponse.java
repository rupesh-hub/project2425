package com.rupesh.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantResponse {

    private String name;
    private String username;
    private String email;
    private String profile;
    private boolean isOnline;
    private LocalDateTime lastSeen;
    private String role;
    private int unreadCount;

}

