package com.rupesh.role.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rupesh.shared.BaseEntity;
import com.rupesh.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_roles")
@EntityListeners(AuditingEntityListener.class)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_role_id_seq_generator")
    @SequenceGenerator(name = "_role_id_seq_generator", sequenceName = "_role_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "role", nullable = false, unique = true)
    private String role;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;

}