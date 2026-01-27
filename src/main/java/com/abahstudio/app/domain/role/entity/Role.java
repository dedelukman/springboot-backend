package com.abahstudio.app.domain.role.entity;

import com.abahstudio.app.core.entity.CompanyScopedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "roles"
)
@Getter
@Setter
public class Role extends CompanyScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code; // SUPER_ADMIN, ADMIN, USER

    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<RolePermission> rolePermissions = new HashSet<>();
}

