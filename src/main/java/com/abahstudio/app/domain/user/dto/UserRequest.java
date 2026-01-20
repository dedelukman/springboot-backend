package com.abahstudio.app.domain.user.dto;


import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Set<UUID> roleIds;
    private Boolean enabled;
    private Boolean locked;
}
