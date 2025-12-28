package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.website.Website;
import com.abahstudio.app.domain.website.WebsiteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebsiteInitializer implements ApplicationRunner {

    private final WebsiteRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            Website site = new Website();
            site.setName("Abah Studio");
            site.setTagline("SaaS Platform");
            site.setDescription("All in one SaaS solution");

            site.setMetaTitle("Abah Studio SaaS");
            site.setMetaDescription("Best SaaS platform");
            site.setMetaKeywords("saas, billing, subscription");

            site.setEmail("info@abahstudio.com");
            site.setPhone("+62xxx");

            repository.save(site);
        }
    }
}

