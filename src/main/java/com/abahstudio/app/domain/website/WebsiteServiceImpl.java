package com.abahstudio.app.domain.website;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsiteServiceImpl implements WebsiteService {

    private final WebsiteRepository repository;

    @Override
    public Website get() {
        return repository.findFirst()
                .orElseThrow(() -> new RuntimeException("Website configuration not found"));
    }

    @Override
    public Website save(Website website) {
        Website existing = repository.findFirst().orElse(null);

        if (existing == null){
            return repository.save(website);
        }

        existing.setName(website.getName());
        existing.setTagline(website.getTagline());
        existing.setDescription(website.getDescription());

        existing.setMetaTitle(website.getMetaTitle());
        existing.setMetaDescription(website.getMetaDescription());
        existing.setMetaKeywords(website.getMetaKeywords());

        existing.setEmail(website.getEmail());
        existing.setPhone(website.getPhone());
        existing.setAddress(website.getAddress());

        return repository.save(existing);
    }
}

