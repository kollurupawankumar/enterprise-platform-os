package com.clinical.workflow.service;

import com.clinical.audit.service.ClinicalAuditService;
import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.service.DoctorConsultationService;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.service.PharmacyService;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PatientCheckoutWorkflowService {

    private final PatientService patientService;
    private final VisitService visitService;
    private final DoctorConsultationService doctorConsultationService;
    private final LabDiagnosticsService labDiagnosticsService;
    private final PharmacyService pharmacyService;
    private final BillingService billingService;
    private final ClinicalAuditService auditService;

    public PatientCheckoutWorkflowService(PatientService patientService,
                                           VisitService visitService,
                                           DoctorConsultationService doctorConsultationService,
                                           LabDiagnosticsService labDiagnosticsService,
                                           PharmacyService pharmacyService,
                                           BillingService billingService,
                                           ClinicalAuditService auditService) {
        this.patientService = patientService;
        this.visitService = visitService;
        this.doctorConsultationService = doctorConsultationService;
        this.labDiagnosticsService = labDiagnosticsService;
        this.pharmacyService = pharmacyService;
        this.billingService = billingService;
        this.auditService = auditService;
    }

    public InvoiceEntity processPatientCheckout(String visitId, String paymentMode, BigDecimal consultationFee, BigDecimal labFee, BigDecimal pharmacyFee) {
        VisitEntity visit = visitService.findByVisitId(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

        // Generate Invoice
        InvoiceEntity invoice = billingService.generateInvoice(visitId, visit.getPatientId(), consultationFee, labFee, pharmacyFee);
        
        // Process Payment
        InvoiceEntity paidInvoice = billingService.processPayment(invoice.getInvoiceId(), paymentMode);

        // Update Visit Status
        visitService.updateStatus(visitId, "COMPLETED");

        // Log Audit Event
        auditService.logEvent("RECEPTIONIST", "CHECKOUT_COMPLETED", "Completed checkout for Visit: " + visitId + ", Total Paid: " + paidInvoice.getTotalAmount());

        return paidInvoice;
    }
}
