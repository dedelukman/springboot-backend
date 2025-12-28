package com.abahstudio.app.domain.subscription.service;

import com.abahstudio.app.domain.subscription.dto.BillingInvoiceResponse;
import com.abahstudio.app.domain.subscription.dto.BillingPlanResponse;
import com.abahstudio.app.domain.subscription.entity.BillingInvoice;

import java.util.List;

public interface BillingService {
    List<BillingPlanResponse> getPlans();
    List<BillingInvoiceResponse> getInvoiceHistory();
    void payInvoice(String invoiceNumber);
    BillingInvoice createInvoice(String planCode);
    BillingInvoice createInvoice(String planCode, String companyCode);
    void activateSubscribe(BillingInvoice invoice);
}
