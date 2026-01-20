package com.abahstudio.app.domain.role.repository;

import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.RolePermission;
import com.abahstudio.app.domain.role.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsByRoleAndPermission(Role role, Permission permission);

}

