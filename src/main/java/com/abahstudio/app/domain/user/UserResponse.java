package com.abahstudio.app.domain.user;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private boolean enabled;
    private boolean locked;

    private String companyCode;
}
