package com.abahstudio.app.core.initializer;

import com.abahstudio.app.domain.subscription.entity.BillingPlan;
import com.abahstudio.app.domain.subscription.repository.BillingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BillingPlanInitializer implements ApplicationRunner {

    private final BillingPlanRepository repo;

    @Override
    public void run(ApplicationArguments args) {
        createIfNotExists("BASIC", "Basic", new BigDecimal("150000"),
                "IDR", "For personal use");

        createIfNotExists("PRO", "Pro", new BigDecimal("350000"),
                "IDR", "For professionals");

        createIfNotExists("ENTERPRISE", "Enterprise", new BigDecimal("750000"),
                "IDR", "For teams");
    }

    private void createIfNotExists(
            String code,
            String name,
            BigDecimal price,
            String currency,
            String desc
    ) {
        repo.findByCodeIgnoreCase(code)
                .orElseGet(() -> {
                    BillingPlan p = new BillingPlan();
                    p.setCode(code);
                    p.setName(name);
                    p.setPrice(price);
                    p.setCurrency(currency);
                    p.setDescription(desc);
                    return repo.save(p);
                });
    }
}

