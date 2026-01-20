package com.abahstudio.app.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByEmail(String superAdminEmail);

    boolean existsByIdAndCompanyCode(UUID id, String companyCode);


    @Query("""
    select distinct u from User u
    left join fetch u.userRoles ur
    left join fetch ur.role r
    left join fetch r.rolePermissions rp
    left join fetch rp.permission
    where u.username = :value or u.email = :value
""")
    Optional<User> findByUsernameOrEmailWithRoles(String value);

}
