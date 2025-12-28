package com.abahstudio.app.domain.subscription.repository;

import com.abahstudio.app.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByCompanyCode(String companyCode);
}
