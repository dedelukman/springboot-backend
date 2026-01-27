package com.abahstudio.app.domain.role.service;

import com.abahstudio.app.core.security.SecurityUtil;
import com.abahstudio.app.domain.role.dto.CreateRoleRequest;
import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.RolePermission;
import com.abahstudio.app.domain.role.repository.PermissionRepository;
import com.abahstudio.app.domain.role.repository.RolePermissionRepository;
import com.abahstudio.app.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final SecurityUtil securityUtil;

    @Override
    public Role createRole(CreateRoleRequest request, String companyCode) {
        if (companyCode == null){
            companyCode = securityUtil.getCompanyCode();
        }

        Role role = new Role();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setCompanyCode(companyCode);

        return roleRepository.save(role);
    }

    @Override
    public void assignPermission(UUID roleId, UUID permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        boolean exists = rolePermissionRepository.exists(
                Example.of(new RolePermission(null, role, permission))
        );

        if (exists) return;

        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(permission);

        rolePermissionRepository.save(rp);
    }

    @Override
    public List<Role> findAllByCompany() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public Role findByCodeAndCompanyCode(String code, String companyCode) {
        return roleRepository.findByCodeAndCompanyCode(code, companyCode)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}

