package com.rupesh.chat.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnreadMessageCountResponse {

    private int count;
    private String username;
    private Long conversationId;

}
