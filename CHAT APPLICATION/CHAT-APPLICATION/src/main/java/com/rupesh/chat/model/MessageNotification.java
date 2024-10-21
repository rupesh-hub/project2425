package com.rupesh.chat.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class MessageNotification {

    /*
     * conversationId
     * messageId
     * content
     * createdOn
     * status
     * */
    private Long conversationId;
    private Long messageId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdOn;

}
