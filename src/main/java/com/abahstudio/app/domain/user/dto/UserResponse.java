package com.abahstudio.app.domain.user.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private boolean enabled;
    private boolean locked;

    private String companyCode;
}