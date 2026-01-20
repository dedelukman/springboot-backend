package com.abahstudio.app.domain.role;

import com.abahstudio.app.domain.role.dto.CreatePermissionRequest;
import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public Permission create(@RequestBody CreatePermissionRequest request) {
        return permissionService.create(request);
    }

    @GetMapping
    public List<Permission> list() {
        return permissionService.findAll();
    }
}

