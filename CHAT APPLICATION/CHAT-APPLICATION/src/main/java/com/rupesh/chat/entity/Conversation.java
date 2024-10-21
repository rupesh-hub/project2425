package com.rupesh.chat.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rupesh.chat.model.ConversationType;
import com.rupesh.shared.BaseEntity;
import com.rupesh.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"participants", "messages"})
@Entity
@Table(name = "_CONVERSATION")
@DynamicUpdate
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONVERSATION_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "CONVERSATION_ID_SEQUENCE_GENERATOR", sequenceName = "CONVERSATION_ID_SEQUENCE", allocationSize = 50, initialValue = 1)
    private Long id;

    private String name;
    private String avatar;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "_conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

}
