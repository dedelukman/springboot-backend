package com.abahstudio.app.domain.role;

import com.abahstudio.app.domain.role.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/{userId}/roles/{roleId}")
    public void assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId
    ) {
        userRoleService.assignRole(userId, roleId, null);
    }
}

