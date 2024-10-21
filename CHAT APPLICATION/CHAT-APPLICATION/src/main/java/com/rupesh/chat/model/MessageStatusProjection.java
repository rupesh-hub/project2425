package com.rupesh.chat.model;


public interface MessageStatusProjection {

    Long getMessageId();
    String getSender();
    Long getConversationId();
    MessageStatus getStatus();

}
