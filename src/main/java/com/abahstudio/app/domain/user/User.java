package com.abahstudio.app.domain.user;

import com.abahstudio.app.domain.BaseEntity;
import com.abahstudio.app.domain.auth.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username; // belum digunakan saat login

    @Column(nullable = false, unique = true)
    private String email; // digunakan untuk login saat ini

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = true;
    private boolean locked = false;

    @PrePersist
    public void prePersist() {
        if (this.username == null) {
            this.username = this.email;
        }
    }



}

