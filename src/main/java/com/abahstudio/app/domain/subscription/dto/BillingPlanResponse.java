package com.abahstudio.app.domain.subscription.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillingPlanResponse {
    private String code;
    private String name;
    private String description;
    private String currency;
    private BigDecimal price;
}
