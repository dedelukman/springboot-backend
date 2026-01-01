package com.abahstudio.app.domain.subscription.service;

import com.abahstudio.app.core.numbering.NumberingService;
import com.abahstudio.app.core.security.SecurityUtil;
import com.abahstudio.app.domain.subscription.InvoiceStatus;
import com.abahstudio.app.domain.subscription.SubscriptionStatus;
import com.abahstudio.app.domain.subscription.dto.BillingInvoiceResponse;
import com.abahstudio.app.domain.subscription.dto.BillingPlanResponse;
import com.abahstudio.app.domain.subscription.entity.BillingInvoice;
import com.abahstudio.app.domain.subscription.entity.BillingPlan;
import com.abahstudio.app.domain.subscription.entity.Subscription;
import com.abahstudio.app.domain.subscription.repository.BillingInvoiceRepository;
import com.abahstudio.app.domain.subscription.repository.BillingPlanRepository;
import com.abahstudio.app.domain.subscription.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingPlanRepository planRepository;
    private final BillingInvoiceRepository invoiceRepository;
    private final SecurityUtil securityUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final NumberingService numberingService;

    @Override
    public List<BillingPlanResponse> getPlans() {
        return planRepository.findAll().stream()
                .map(p -> {
                    BillingPlanResponse r = new BillingPlanResponse();
                    r.setCode(p.getCode());
                    r.setName(p.getName());
                    r.setDescription(p.getDescription());
                    r.setPrice(p.getPrice());
                    r.setCurrency(p.getCurrency());
                    return r;
                })
                .toList();
    }

    @Override
    public List<BillingInvoiceResponse> getInvoiceHistory() {
        return invoiceRepository
                .findByCompanyCodeOrderByInvoiceDateDesc(
                        securityUtil.getCompanyCode()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void payInvoice(String invoiceNumber) {
        BillingInvoice invoice = invoiceRepository
                .findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice already paid");
        }

        if (invoice.getStatus() == InvoiceStatus.EXPIRED) {
            throw new RuntimeException("Invoice has expired");
        }

        invoice.setStatus(InvoiceStatus.PAID);

        Subscription sub = subscriptionRepository
                .findByCompanyCode(invoice.getCompanyCode())
                .orElseThrow(() -> new RuntimeException(
                        "Subscription not found for company: " + invoice.getCompanyCode()
                ));

        if (invoice.getAmount().compareTo(sub.getBillingPlan().getPrice()) > 0){
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setPlanCode(invoice.getPlanCode());
            sub.setStartDate(invoice.getPeriodStart());
            sub.setEndDate(invoice.getPeriodEnd());
        }


        subscriptionRepository.save(sub);


    }

    @Override
    public BillingInvoice createInvoice(String planCode) {
        BillingPlan plan = planRepository.findByCode(planCode)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        String companyCode = securityUtil.getCompanyCode();

        BillingInvoice invoice = invoiceRepository.findLatestUnpaidByCompanyCode(companyCode)
                .orElse(new BillingInvoice());

        Subscription sub = subscriptionRepository
                .findByCompanyCode(companyCode)
                .orElseThrow();

        if (plan.getPrice().compareTo(sub.getBillingPlan().getPrice()) > 0){
            invoice.setPeriodStart(LocalDate.now());
            invoice.setPeriodEnd(LocalDate.now().plusMonths(1));
        }else{
            invoice.setPeriodStart(sub.getEndDate());
            invoice.setPeriodEnd(sub.getEndDate().plusMonths(1));
        }

        if (invoice.getInvoiceNumber() == null) {
            invoice.setInvoiceNumber(numberingService.generateNumber("INVOICE_BY_SYSTEM"));
            invoice.setInvoiceDate(LocalDate.now());
        }
        invoice.setCompanyCode(companyCode);
        invoice.setPlanCode(plan.getCode());
        invoice.setAmount(plan.getPrice());
        invoice.setCurrency(plan.getCurrency());
        invoice.setStatus(InvoiceStatus.UNPAID);

        return invoiceRepository.save(invoice);
    }

    @Override
    public void activateSubscribe(BillingInvoice invoice) {
        Subscription sub = subscriptionRepository
                .findByCompanyCode(invoice.getCompanyCode())
                .orElseThrow();

        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setEndDate(sub.getEndDate().plusMonths(1));
    }

    @Override
    public BillingInvoice createInvoice(String planCode, String companyCode) {

        BillingPlan plan = planRepository.findByCode(planCode)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        BillingInvoice invoice = new BillingInvoice();
        invoice.setInvoiceNumber(numberingService.generateNumber("INVOICE_BY_SYSTEM"));
        invoice.setCompanyCode(companyCode);
        invoice.setPlanCode(plan.getCode());
        invoice.setAmount(plan.getPrice());
        invoice.setCurrency(plan.getCurrency());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setPeriodStart(LocalDate.now());
        invoice.setPeriodEnd(LocalDate.now().plusMonths(1));

        return invoiceRepository.save(invoice);
    }


    private BillingInvoiceResponse toResponse(BillingInvoice i) {
        BillingInvoiceResponse r = new BillingInvoiceResponse();
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setInvoiceDate(i.getInvoiceDate());
        r.setPlan(i.getPlanCode());
        r.setAmount(i.getAmount());
        r.setStatus(i.getStatus());
        r.setCurrency(i.getCurrency());
        return r;
    }
}

