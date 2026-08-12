package com.clinical.workflow.service;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import com.clinical.followup.entity.FollowupEntity;
import com.clinical.followup.service.FollowupService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class PatientCheckoutWorkflowService {

    private final VisitService visitService;
    private final BillingService billingService;
    private final FollowupService followupService;

    public PatientCheckoutWorkflowService(VisitService visitService,
                                         BillingService billingService,
                                         FollowupService followupService) {
        this.visitService = visitService;
        this.billingService = billingService;
        this.followupService = followupService;
    }

    public InvoiceEntity processPatientCheckout(String visitId, Integer followupDays) {
        return processPatientCheckout(visitId, "CASH", BigDecimal.valueOf(500), BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public InvoiceEntity processPatientCheckout(String visitId, String paymentMethod, BigDecimal consultationFee, BigDecimal pharmacyFee, BigDecimal labFee) {
        Optional<VisitEntity> visitOpt = visitService.getActiveVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst();

        if (visitOpt.isEmpty()) {
            throw new IllegalArgumentException("Active visit not found: " + visitId);
        }

        VisitEntity visit = visitOpt.get();

        // 1. Generate Consolidated Bill
        InvoiceEntity invoice = billingService.generateInvoice(
                visit.getPatientId(),
                visit.getVisitId(),
                consultationFee,
                pharmacyFee,
                labFee
        );

        // 2. Mark Paid
        billingService.processPayment(invoice.getInvoiceId(), paymentMethod);

        // 3. Mark Visit Checked Out
        visitService.updateStatus(visitId, "CHECKED_OUT");

        return invoice;
    }
}
