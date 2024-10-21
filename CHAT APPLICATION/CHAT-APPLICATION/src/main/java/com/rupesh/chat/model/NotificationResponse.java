package com.rupesh.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private Long id;
    private LocalDateTime timestamp;
    private String action;
    private String actionBy;
    private String actionByProfile;
    private String recipient;

}
