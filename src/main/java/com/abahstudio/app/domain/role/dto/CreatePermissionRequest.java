package com.abahstudio.app.domain.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequest {
    private String code;
    private String name;
}

