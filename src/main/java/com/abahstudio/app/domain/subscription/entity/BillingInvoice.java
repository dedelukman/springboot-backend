package com.abahstudio.app.domain.subscription.entity;

import com.abahstudio.app.core.entity.CompanyScopedEntity;
import com.abahstudio.app.domain.subscription.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billing_invoices")
@Getter
@Setter
public class BillingInvoice extends CompanyScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber; // INV-2025120001

    private String planCode;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;


    private LocalDate invoiceDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String currency;
}
