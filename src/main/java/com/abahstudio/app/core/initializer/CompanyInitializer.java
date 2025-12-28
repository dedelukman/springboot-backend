package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.company.Company;
import com.abahstudio.app.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyInitializer implements ApplicationRunner {

    private final CompanyRepository companyRepository;

    public static final String SYSTEM_COMPANY_CODE = "SYSTEM";

    @Override
    public void run(ApplicationArguments args) {
        companyRepository.findByCode(SYSTEM_COMPANY_CODE)
                .ifPresentOrElse(
                        c -> log.info("SYSTEM company already exists"),
                        this::createSystemCompany
                );
    }

    private void createSystemCompany() {
        Company company = new Company();
        company.setCode(SYSTEM_COMPANY_CODE);
        company.setName("System Company");
        company.setEmail("system@abahstudio.com");
        company.setPhone("-");
        company.setAddress("-");

        companyRepository.save(company);
        log.info("SYSTEM company created");
    }
}
