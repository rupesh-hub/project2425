package com.rupesh.chat.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {

    private Long id;
    private String sender;
    private String receiver;
    private String content;

}
