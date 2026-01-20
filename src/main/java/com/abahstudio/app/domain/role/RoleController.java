package com.abahstudio.app.domain.role;

import com.abahstudio.app.domain.role.dto.CreateRoleRequest;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public Role create(@RequestBody CreateRoleRequest request) {
        return roleService.createRole(request, null);
    }

    @GetMapping
    public List<Role> list() {
        return roleService.findAllByCompany();
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public void assignPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId
    ) {
        roleService.assignPermission(roleId, permissionId);
    }
}

