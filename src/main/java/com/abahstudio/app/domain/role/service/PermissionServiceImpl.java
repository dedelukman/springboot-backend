package com.abahstudio.app.domain.role.service;

import com.abahstudio.app.domain.role.dto.CreatePermissionRequest;
import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Permission create(CreatePermissionRequest request) {

        Permission permission = new Permission();
        permission.setCode(request.getCode());

        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
}
