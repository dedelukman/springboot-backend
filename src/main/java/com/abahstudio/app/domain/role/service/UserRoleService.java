package com.abahstudio.app.domain.role.service;

import java.util.UUID;

public interface UserRoleService {

    void assignRole(UUID userId, UUID roleId, String companyCode);
}

