package com.abahstudio.app.domain.user;


import com.abahstudio.app.core.entity.CompanyScopedEntity;
import com.abahstudio.app.domain.role.entity.UserRole;
import com.abahstudio.app.domain.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User extends CompanyScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String username; // belum digunakan saat login

    @Column(nullable = false, unique = true)
    private String email; // digunakan untuk login saat ini

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String phone;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>();

    private boolean enabled = true;
    private boolean locked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @PrePersist
    public void prePersist() {
        if (this.username == null) {
            this.username = this.email;
        }
    }



}

