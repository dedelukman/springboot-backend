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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Pastikan dijalankan setelah PermissionInitializer
@Transactional
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final UserRoleRepository userRoleRepo;
    private final SuperAdminProperties props;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository rolePermissionRepo;

    private static final String SYSTEM_COMPANY_CODE = "SYSTEM";

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting role initialization with permissions...");

        initializeSystemRolesWithPermissions();


        log.info("Role initialization with permissions completed");
    }

    private void initializeSystemRolesWithPermissions() {
        // 1. SUPER_ADMIN - mendapatkan semua permissions
        Role superAdminRole = createOrGetRole("SUPER_ADMIN", "Super Administrator", SYSTEM_COMPANY_CODE);
        assignSuperAdminPermissions(superAdminRole);

        // 2. ADMIN - mendapatkan subset permissions tertentu
        Role adminRole = createOrGetRole("ADMIN", "Administrator", SYSTEM_COMPANY_CODE);
        assignAdminPermissions(adminRole);

        Role userRole = createOrGetRole("USER", "User", SYSTEM_COMPANY_CODE);
        assignUserPermissions(userRole);

        Company company = companyRepo.findByCode(SYSTEM_COMPANY_CODE)
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setCode(SYSTEM_COMPANY_CODE);
                    c.setName(SYSTEM_COMPANY_CODE);
                    return companyRepo.save(c);
                });
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
            ur.setCompanyCode(SYSTEM_COMPANY_CODE);
            userRoleRepo.save(ur);
        }

        log.info("SUPER_ADMIN created with custom permissions");
    }

    private Role createOrGetRole(String code, String name, String companyCode) {
        return roleRepository.findByCodeAndCompanyCode(code, companyCode)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setCode(code);
                    role.setName(name);
                    role.setCompanyCode(companyCode);
                    return roleRepository.save(role);
                });
    }

    private void assignAllPermissionsToRole(Role role) {
        // Ambil semua permissions dari database
        List<Permission> allPermissions = permissionRepository.findAll();

        // Buat RolePermission untuk setiap permission
        Set<RolePermission> rolePermissions = new HashSet<>();
        for (Permission permission : allPermissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermissions.add(rolePermission);
        }

        role.setRolePermissions(rolePermissions);
        roleRepository.save(role);
        log.info("Assigned all {} permissions to SUPER_ADMIN role", allPermissions.size());
    }

    private void assignSuperAdminPermissions(Role role) {
        // Definisikan permission codes untuk ADMIN
        List<String> adminPermissionCodes = List.of(
                "DASHBOARD_VIEW",
                "SETTING_VIEW",
                "COMPANY_INFO_VIEW",
                "COMPANY_INFO_UPDATE",
                "WEBSITE_INFO_VIEW",
                "WEBSITE_INFO_UPDATE"

        );

        assignPermissionsByCodes(role, adminPermissionCodes);
    }

    private void assignAdminPermissions(Role role) {
        // Definisikan permission codes untuk ADMIN
        List<String> adminPermissionCodes = List.of(
                "DASHBOARD_VIEW",
                "SETTING_VIEW",
                "COMPANY_INFO_VIEW",
                "COMPANY_INFO_UPDATE",
                "BILLING_VIEW",
                "GETHELP_VIEW"

        );

        assignPermissionsByCodes(role, adminPermissionCodes);
    }

    private void assignUserPermissions(Role role) {
        // Definisikan permission codes untuk USER
        List<String> userPermissionCodes = List.of(
                "DASHBOARD_VIEW",
                "GETHELP_VIEW"
        );

        assignPermissionsByCodes(role, userPermissionCodes);
    }

    private void assignPermissionsByCodes(Role role, List<String> permissionCodes) {

        for (String code : permissionCodes) {
            permissionRepository.findByCode(code)
                    .ifPresent(permission -> {
                        rolePermissionRepo.save(
                                new RolePermission(role, permission)
                        );
                    });
        }

    }
}
