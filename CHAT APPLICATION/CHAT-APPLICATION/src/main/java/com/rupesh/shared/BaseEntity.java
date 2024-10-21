package com.rupesh.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    protected Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedOn;

    @Column(nullable = false, updatable = false)
    protected String createdBy;

    @Column
    protected String modifiedBy;

    @Column(nullable = false)
    protected boolean enabled = false;

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        createdBy = authenticatedUser();
    }

    @PreUpdate
    protected void onModify() {
        modifiedOn = new Date();
        modifiedBy = authenticatedUser();
    }

    protected String authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return (authentication != null && authentication.isAuthenticated()) ? authentication.getName(): null;
    }

}
