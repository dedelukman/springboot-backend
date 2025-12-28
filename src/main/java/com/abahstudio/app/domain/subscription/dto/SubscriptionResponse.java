package com.abahstudio.app.domain.subscription.dto;

import com.abahstudio.app.domain.subscription.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionResponse {
    private String plan;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}

