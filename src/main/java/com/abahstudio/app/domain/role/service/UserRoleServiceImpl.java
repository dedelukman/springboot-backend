package com.abahstudio.app.domain.role.service;

import com.abahstudio.app.core.security.SecurityUtil;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.role.entity.UserRole;
import com.abahstudio.app.domain.role.repository.RoleRepository;
import com.abahstudio.app.domain.role.repository.UserRoleRepository;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityUtil securityUtil;

    @Override
    public void assignRole(UUID userId, UUID roleId, String companyCode) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            return;
        }

        if (companyCode == null){
            companyCode = securityUtil.getCompanyCode();
        }

        UserRole ur = new UserRole();
        ur.setUser(user);
        ur.setRole(role);
        ur.setCompanyCode(companyCode);

        userRoleRepository.save(ur);
    }
}

