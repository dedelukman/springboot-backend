package com.abahstudio.app.domain.user.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Set<String> roles;        // SUPER_ADMIN, ADMIN
    private Set<String> permissions;  // USER_CREATE, ...
    private boolean enabled;
    private boolean locked;

    private String companyCode;
    private String avatarUrl;
}