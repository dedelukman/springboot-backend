package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.RolePermission;
import com.abahstudio.app.domain.role.repository.PermissionRepository;
import com.abahstudio.app.domain.role.repository.RolePermissionRepository;
import com.abahstudio.app.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RolePermissionInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Create default permissions
        Map<String, Permission> permissions = createPermissions();

        // Create default roles
        Map<String, Role> roles = createRoles();

        // Assign permissions to roles
        assignPermissionsToRoles(roles, permissions);
    }

    private Map<String, Permission> createPermissions() {
        List<String> permissionCodes = Arrays.asList(
                "DASHBOARD_VIEW",
                "SETTING_VIEW",
                "GETHELP_VIEW",
                "BILLING_VIEW",
                "COMPANY_INFO_VIEW",
                "WEBSITE_INFO_VIEW",
                "COMPANY_INFO_UPDATE",
                "WEBSITE_INFO_UPDATE"

        );

        Map<String, Permission> permissionMap = new HashMap<>();

        for (String code : permissionCodes) {
            Permission permission = permissionRepository.findByCode(code)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setCode(code);
                        return permissionRepository.save(newPermission);
                    });
            permissionMap.put(code, permission);
        }

        return permissionMap;
    }

    private Map<String, Role> createRoles() {
        List<RoleData> roleDataList = Arrays.asList(
                new RoleData("SUPER_ADMIN", "Super Administrator", "SYSTEM"), // null = global role
                new RoleData("ADMIN", "Administrator", "SYSTEM"),
                new RoleData("USER", "Regular User", "SYSTEM")
        );

        Map<String, Role> roleMap = new HashMap<>();

        for (RoleData roleData : roleDataList) {
            Role role = roleRepository.findByCode(roleData.code)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setCode(roleData.code);
                        newRole.setName(roleData.name);
                        newRole.setCompanyCode(roleData.companyCode); // Set company ID if needed
                        return roleRepository.save(newRole);
                    });
            roleMap.put(roleData.code, role);
        }

        return roleMap;
    }

    private void assignPermissionsToRoles(Map<String, Role> roles, Map<String, Permission> permissions) {
        // SUPER_ADMIN gets all permissions
        assignAllPermissionsToRole(roles.get("SUPER_ADMIN"), permissions);

        // ADMIN gets subset of permissions
        assignAdminPermissions(roles.get("ADMIN"), permissions);

        // USER gets basic permissions
        assignUserPermissions(roles.get("USER"), permissions);
    }

    private void assignAllPermissionsToRole(Role role, Map<String, Permission> permissions) {
        List<String> adminPermissionCodes = Arrays.asList(
                "DASHBOARD_VIEW",
                "SETTING_VIEW",
                "COMPANY_INFO_VIEW",
                "WEBSITE_INFO_VIEW",
                "COMPANY_INFO_UPDATE",
                "WEBSITE_INFO_UPDATE"
        );

        for (String code : adminPermissionCodes) {
            Permission permission = permissions.get(code);
            if (permission != null) {
                assignPermissionToRoleIfNotExists(role, permission);
            }
        }
    }

    private void assignAdminPermissions(Role role, Map<String, Permission> permissions) {
        List<String> adminPermissionCodes = Arrays.asList(
                "DASHBOARD_VIEW",
                "SETTING_VIEW",
                "GETHELP_VIEW",
                "BILLING_VIEW",
                "COMPANY_INFO_VIEW",
                "COMPANY_INFO_UPDATE"
        );

        for (String code : adminPermissionCodes) {
            Permission permission = permissions.get(code);
            if (permission != null) {
                assignPermissionToRoleIfNotExists(role, permission);
            }
        }
    }

    private void assignUserPermissions(Role role, Map<String, Permission> permissions) {
        List<String> userPermissionCodes = Arrays.asList(
                "DASHBOARD_VIEW",
                "GETHELP_VIEW",
                "BILLING_VIEW"

        );

        for (String code : userPermissionCodes) {
            Permission permission = permissions.get(code);
            if (permission != null) {
                assignPermissionToRoleIfNotExists(role, permission);
            }
        }
    }

    private void assignPermissionToRoleIfNotExists(Role role, Permission permission) {
        boolean exists = rolePermissionRepository.existsByRoleAndPermission(
                role,
                permission
        );

        if (!exists) {
            RolePermission rolePermission = new RolePermission(role, permission);
            rolePermissionRepository.save(rolePermission);

            // Initialize the set if null
            if (role.getRolePermissions() == null) {
                role.setRolePermissions(new HashSet<>());
            }
            role.getRolePermissions().add(rolePermission);
        }
    }

    // Helper class for role data
    private static class RoleData {
        String code;
        String name;
        String companyCode;

        RoleData(String code, String name, String companyCode) {
            this.code = code;
            this.name = name;
            this.companyCode = companyCode;
        }
    }
}
