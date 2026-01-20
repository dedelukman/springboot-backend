package com.abahstudio.app.domain.role.service;

import com.abahstudio.app.domain.role.dto.CreateRoleRequest;
import com.abahstudio.app.domain.role.entity.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    Role createRole(CreateRoleRequest request, String companyCode);

    void assignPermission(UUID roleId, UUID permissionId);

    List<Role> findAllByCompany();

    Role findById(UUID id);
}

