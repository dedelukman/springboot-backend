package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.company.Company;
import com.abahstudio.app.domain.company.CompanyRepository;
import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.RolePermission;
import com.abahstudio.app.domain.role.entity.UserRole;
import com.abahstudio.app.domain.role.repository.PermissionRepository;
import com.abahstudio.app.domain.role.repository.RolePermissionRepository;
import com.abahstudio.app.domain.role.repository.RoleRepository;
import com.abahstudio.app.domain.role.repository.UserRoleRepository;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermissionRepo;
    private final UserRoleRepository userRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final SuperAdminProperties props;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 1️⃣ COMPANY SYSTEM
        Company company = companyRepo.findByCode("SYSTEM")
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setCode("SYSTEM");
                    c.setName("System");
                    return companyRepo.save(c);
                });

        // 2️⃣ PERMISSIONS (seed once)
        List<String> permissions = List.of(
                "SUPER_ADMIN_MENU_VIEW",
                "USER_CREATE",
                "USER_READ",
                "USER_UPDATE",
                "USER_DELETE"
        );

        Map<String, Permission> permissionMap = new HashMap<>();
        for (String code : permissions) {
            Permission p = permissionRepo.findByCode(code)
                    .orElseGet(() -> permissionRepo.save(new Permission(code)));
            permissionMap.put(code, p);
        }

        // 3️⃣ ROLE SUPER_ADMIN
        Role superAdminRole = roleRepo
                .findByCodeAndCompanyCode("SUPER_ADMIN", company.getCode())
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setCode("SUPER_ADMIN");
                    r.setName("Super Administrator");
                    r.setCompanyCode(company.getCode());
                    return roleRepo.save(r);
                });

        // 4️⃣ ROLE → PERMISSION
        for (Permission p : permissionMap.values()) {
            rolePermissionRepo.save(
                    new RolePermission(superAdminRole, p)
            );
        }

        // 5️⃣ USER SUPER ADMIN
        if (userRepo.existsByEmail(props.getEmail())) {
            return;
        }

        User user = new User();
        user.setEmail(props.getEmail());
        user.setUsername(props.getUsername());
        user.setFullName("SUPER ADMIN");
        user.setPassword(passwordEncoder.encode(props.getPassword()));
        user.setCompany(company);
        user.setCompanyCode(company.getCode());
        user.setEnabled(true);
        user.setLocked(false);

        userRepo.save(user);

        // 6️⃣ USER → ROLE
        if (!userRoleRepo.existsByUserAndRole(user, superAdminRole)) {
            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setRole(superAdminRole);
            ur.setCompanyCode(company.getCode());
            userRoleRepo.save(ur);
        }

        log.info("SUPER_ADMIN created with full permissions");
    }
}


