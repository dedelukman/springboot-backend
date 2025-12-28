package com.abahstudio.app.domain.subscription.repository;

import com.abahstudio.app.domain.subscription.entity.BillingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BillingInvoiceRepository extends JpaRepository<BillingInvoice, Long> {

    List<BillingInvoice> findByCompanyCodeOrderByInvoiceDateDesc(
            String companyCode
    );

    Optional<BillingInvoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT bi FROM BillingInvoice bi WHERE bi.companyCode = :companyCode AND bi.status = 'UNPAID' ORDER BY bi.invoiceDate DESC LIMIT 1")
    Optional<BillingInvoice> findLatestUnpaidByCompanyCode(@Param("companyCode") String companyCode);
}
