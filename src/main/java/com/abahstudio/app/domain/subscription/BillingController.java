package com.abahstudio.app.domain.subscription;

import com.abahstudio.app.domain.subscription.dto.BillingInvoiceResponse;
import com.abahstudio.app.domain.subscription.dto.BillingPlanResponse;
import com.abahstudio.app.domain.subscription.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/plans")
    public List<BillingPlanResponse> plans() {
        return billingService.getPlans();
    }

    @GetMapping("/invoices")
    public List<BillingInvoiceResponse> invoices() {
        return billingService.getInvoiceHistory();
    }

    @PostMapping("/invoices/{planCode}")
    public void createInvoices(@PathVariable String planCode) {
         billingService.createInvoice(planCode);
    }

    @PostMapping("/pay/{invoiceNumber}")
    public void pay(@PathVariable String invoiceNumber) {
        billingService.payInvoice(invoiceNumber);
    }
}

