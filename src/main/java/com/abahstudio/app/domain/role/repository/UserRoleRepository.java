package com.abahstudio.app.domain.role.repository;

import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.UserRole;
import com.abahstudio.app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    boolean existsByUserAndRole(User user, Role role);
}
