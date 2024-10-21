package com.rupesh.chat.entity;

import com.rupesh.user.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Suggestion {
    private Long id;
    private User user;
}