package com.abahstudio.app.domain.subscription.dto;

import com.abahstudio.app.domain.subscription.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BillingInvoiceResponse {
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String plan;
    private BigDecimal amount;
    private InvoiceStatus status;
    private String currency;
}

