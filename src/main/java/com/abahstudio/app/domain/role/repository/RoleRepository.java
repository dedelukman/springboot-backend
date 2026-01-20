package com.abahstudio.app.domain.role.repository;

import com.abahstudio.app.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCodeAndCompanyCode(String code, String companyCode);
}
