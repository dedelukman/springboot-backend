package com.abahstudio.app.domain.user;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String role;
    private Boolean enabled;
    private Boolean locked;
}
