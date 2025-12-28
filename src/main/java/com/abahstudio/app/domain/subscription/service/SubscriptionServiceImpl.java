package com.abahstudio.app.domain.subscription.service;

import com.abahstudio.app.core.security.SecurityUtil;
import com.abahstudio.app.domain.company.CompanyRepository;
import com.abahstudio.app.domain.subscription.SubscriptionStatus;
import com.abahstudio.app.domain.subscription.dto.SubscriptionResponse;
import com.abahstudio.app.domain.subscription.entity.BillingInvoice;
import com.abahstudio.app.domain.subscription.entity.BillingPlan;
import com.abahstudio.app.domain.subscription.entity.Subscription;
import com.abahstudio.app.domain.subscription.repository.BillingPlanRepository;
import com.abahstudio.app.domain.subscription.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final BillingPlanRepository planRepository;
    private final CompanyRepository companyRepository;
    private final BillingService billingService;
    private final SecurityUtil securityUtil;

    @Override
    public SubscriptionResponse getCurrent() {
        Subscription s = repository
                .findByCompanyCode(securityUtil.getCompanyCode())
                .orElse(null);

        if (s == null) return null;

        SubscriptionResponse r = new SubscriptionResponse();
        r.setPlan(s.getPlanCode());
        r.setStatus(s.getStatus());
        r.setStartDate(s.getStartDate());
        r.setEndDate(s.getEndDate());
        return r;
    }

//    @Override
//    public void subscribe(String planCode) {
//
//        BillingPlan plan = planRepository.findByCode(planCode)
//                .orElseThrow(() -> new RuntimeException("Plan not found"));
//
//        Subscription sub = repository
//                .findByCompanyCode(securityUtil.getCompanyCode())
//                .orElse(new Subscription());
//
//        sub.setCompanyCode(securityUtil.getCompanyCode());
//        sub.setPlanCode(plan.getCode());
//        sub.setStatus(SubscriptionStatus.PENDING);
//        sub.setStartDate(LocalDate.now());
//        sub.setEndDate(LocalDate.now().plusMonths(1));
//
//        repository.save(sub);
//
//        // 👉 buat invoice
//        billingService.createInvoice(sub, plan);
//    }

    @Override
    public void activateFromInvoice(BillingInvoice invoice) {
        Subscription sub = repository
                .findByCompanyCode(invoice.getCompanyCode())
                .orElseThrow();

        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setEndDate(sub.getEndDate().plusMonths(1));
    }

    @Override
    public void suspendIfExpired() {
        repository.findAll().forEach(sub -> {
            if (sub.getEndDate().isBefore(LocalDate.now())) {
                sub.setStatus(SubscriptionStatus.EXPIRED);
            }
        });
    }

    @Override
    public void subscribe(String companyCode) {
        BillingPlan plan = planRepository.findByCode("BASIC")
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Subscription sub = new Subscription();

        sub.setCompanyCode(companyCode);
        sub.setPlanCode(plan.getCode());
        sub.setBillingPlan(plan);
        sub.setStatus(SubscriptionStatus.PENDING);
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusMonths(1));

        repository.save(sub);

        billingService.createInvoice(plan.getCode(), companyCode);
    }
}

