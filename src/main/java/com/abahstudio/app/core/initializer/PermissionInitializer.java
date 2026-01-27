package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.role.entity.Permission;
import com.abahstudio.app.domain.role.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionInitializer implements ApplicationRunner {

    private final PermissionRepository permissionRepository;

    private static final List<String> PERMISSION_CODES = List.of(
            "DASHBOARD_VIEW",
            "SETTING_VIEW",
            "GETHELP_VIEW",
            "BILLING_VIEW",
            "COMPANY_INFO_VIEW",
            "WEBSITE_INFO_VIEW",
            "COMPANY_INFO_UPDATE",
            "WEBSITE_INFO_UPDATE"
    );

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting permission initialization...");

        Map<String, Permission> permissionMap = new HashMap<>();

        for (String code : PERMISSION_CODES) {
            Permission permission = permissionRepository.findByCode(code)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setCode(code);
                        return permissionRepository.save(newPermission);
                    });
            permissionMap.put(code, permission);
        }


        log.info("Permission initialization completed. Total permissions: {}", permissionMap.size());
    }

}
