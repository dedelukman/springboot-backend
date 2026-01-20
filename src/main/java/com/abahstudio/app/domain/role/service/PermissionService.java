package com.abahstudio.app.domain.role.service;

import com.abahstudio.app.domain.role.dto.CreatePermissionRequest;
import com.abahstudio.app.domain.role.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission create(CreatePermissionRequest request);

    List<Permission> findAll();
}

