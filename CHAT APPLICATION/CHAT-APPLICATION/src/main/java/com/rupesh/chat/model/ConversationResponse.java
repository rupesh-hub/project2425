package com.rupesh.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class ConversationResponse {

    private Long id;
    private String name;
    private String avatar;
    private Date createdOn;
    private String createdBy;
    private Date updatedOn;
    private String updatedBy;
    private boolean isGroup;
    private boolean isOnline;
    private LocalDateTime lastSeen;
    private Long lastMessageId;
    private String lastMessageContent;
    private Date lastMessageSentTime;
    private MessageStatus messageStatus;
    private String lastMessageSender;

    private Set<ParticipantResponse> participants;
    private List<MessageResponse> messages;

}