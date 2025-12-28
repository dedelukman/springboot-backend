package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.auth.Role;
import com.abahstudio.app.domain.company.Company;
import com.abahstudio.app.domain.company.CompanyRepository;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuperAdminProperties props;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmail(props.getEmail())) {
            return;
        }

        Company company = companyRepository.findByCode("SYSTEM")
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setCode("SYSTEM");
                    c.setName("System");
                    return companyRepository.save(c);
                });

        User superAdmin = User.builder()
                .email(props.getEmail())
                .username(props.getUsername())
                .fullName(Role.SUPER_ADMIN.name())
                .password(passwordEncoder.encode(props.getPassword()))
                .role(Role.SUPER_ADMIN)
                .company(company)
                .enabled(true)
                .locked(false)
                .build();

        superAdmin.setCompanyCode(company.getCode());

        userRepository.save(superAdmin);

        log.info("SUPER_ADMIN user created: {}", superAdmin.getFullName());
    }
}

