package com.abahstudio.app.domain.subscription.repository;

import com.abahstudio.app.domain.subscription.entity.BillingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingPlanRepository extends JpaRepository<BillingPlan, Long> {

    List<BillingPlan> findAll();
    Optional<BillingPlan> findByCode(String code);
    Optional<BillingPlan> findByCodeIgnoreCase(String code);
}

