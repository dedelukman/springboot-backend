package com.abahstudio.app.domain.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequest {
    private String code;
    private String name;
}

