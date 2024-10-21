package com.rupesh.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Participant {

    private String username;
    private String role;
    private int unreadCount;
    private String profile;
    private boolean online;

}
