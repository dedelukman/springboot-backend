package com.abahstudio.app.domain.role.entity;

import com.abahstudio.app.core.entity.CompanyScopedEntity;
import com.abahstudio.app.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class UserRole extends CompanyScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;
}

