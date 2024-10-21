package com.rupesh.chat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rupesh.chat.model.MessageStatus;
import com.rupesh.chat.model.MessageType;
import com.rupesh.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"conversation", "status", "replyToMessage", "attachment"})
@Entity
@Table(name = "_MESSAGE")
@DynamicUpdate
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "MESSAGE_ID_SEQUENCE", sequenceName = "MESSAGE_ID_SEQUENCE", allocationSize = 50, initialValue = 1)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String sender;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @JsonBackReference
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.SENT;

    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "reply_to_message_id")
    private Message replyToMessage;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Attachment attachment;

}
