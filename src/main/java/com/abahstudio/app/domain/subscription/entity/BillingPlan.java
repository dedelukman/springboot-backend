package com.abahstudio.app.domain.subscription.entity;

import com.abahstudio.app.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_plans")
@Getter
@Setter
public class BillingPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // BASIC, PRO, ENTERPRISE

    private String name;
    private String description;
    private String currency;
    private BigDecimal price; // per bulan
}

