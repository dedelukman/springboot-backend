package com.abahstudio.app.domain.subscription.service;

import com.abahstudio.app.domain.subscription.dto.SubscriptionResponse;
import com.abahstudio.app.domain.subscription.entity.BillingInvoice;

public interface SubscriptionService {

    SubscriptionResponse getCurrent();

    void subscribe(String companyCode);

    void activateFromInvoice(BillingInvoice invoice);

    void suspendIfExpired();
}
