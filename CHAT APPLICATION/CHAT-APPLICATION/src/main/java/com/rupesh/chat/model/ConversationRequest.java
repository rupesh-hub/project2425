package com.rupesh.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationRequest {

    private String name;
    private String avatar;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    String[] participants;

}
