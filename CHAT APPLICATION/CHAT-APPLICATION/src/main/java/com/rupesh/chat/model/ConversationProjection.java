package com.rupesh.chat.model;

import java.util.Date;

public interface ConversationProjection {
    Long getId();
    String getConversationType();
    String getName();
    String getAvatar();
    Boolean getIsOnline();
    Long getLastMessageId();
    String getLastMessageContent();
    Date getLastMessageSentTime();
    String getMessageStatus();
    String getLastMessageSender();
}