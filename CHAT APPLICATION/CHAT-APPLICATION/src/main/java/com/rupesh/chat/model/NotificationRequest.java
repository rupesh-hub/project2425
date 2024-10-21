package com.rupesh.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    private String action;
    private String actionBy;
    private String recipient;

}
