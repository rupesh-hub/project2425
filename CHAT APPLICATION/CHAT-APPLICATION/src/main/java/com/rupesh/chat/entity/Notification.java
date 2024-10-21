package com.rupesh.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="NOTIFICATIONS")
@DynamicUpdate
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIFICATION_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "NOTIFICATION_ID_SEQUENCE_GENERATOR", sequenceName = "NOTIFICATION_ID_SEQUENCE", allocationSize = 50, initialValue = 1)
    private Long id;
    private LocalDateTime timestamp;
    private String action;

    private String actionBy;
    private String recipient;

}
