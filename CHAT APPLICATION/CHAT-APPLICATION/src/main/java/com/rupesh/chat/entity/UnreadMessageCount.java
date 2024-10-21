package com.rupesh.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unread_message_count")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnreadMessageCount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UMC_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "UMC_ID_SEQUENCE_GENERATOR", sequenceName = "UMC_ID_SEQUENCE", allocationSize = 50, initialValue = 1)
    private Long id;

    private String username;
    private Long conversationId;
    private Integer unreadCount;
}
