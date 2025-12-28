package com.abahstudio.app.domain.subscription;

import com.abahstudio.app.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 1 * * ?") // tiap jam 1 pagi
    public void checkExpired() {
        subscriptionService.suspendIfExpired();
    }
}

