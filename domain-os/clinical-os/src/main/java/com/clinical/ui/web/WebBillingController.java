package com.clinical.ui.web;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebBillingController {

    private final BillingService billingService;
    private final PatientService patientService;

    public WebBillingController(BillingService billingService, PatientService patientService) {
        this.billingService = billingService;
        this.patientService = patientService;
    }

    @GetMapping("/billing")
    public String listBilling(Model model) {
        model.addAttribute("pageTitle", "Master POS Billing Workstation");
        model.addAttribute("activeTab", "billing");

        List<InvoiceEntity> invoices = billingService.getAllInvoices();
        model.addAttribute("invoices", invoices);

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        return "billing";
    }

    @PostMapping("/billing/create")
    public String createBill(
            @RequestParam("patientId") String patientIdStr,
            @RequestParam(value = "consultationFee", defaultValue = "0.0") Double consultationFee,
            @RequestParam(value = "labFee", defaultValue = "0.0") Double labFee,
            @RequestParam(value = "pharmacyFee", defaultValue = "0.0") Double pharmacyFee,
            @RequestParam(value = "paymentMode", defaultValue = "CASH") String paymentMode) {

        PatientEntity p = patientService.findByPatientId(patientIdStr).orElse(null);
        if (p == null) {
            try {
                Long id = Long.parseLong(patientIdStr);
                p = patientService.getAllPatients().stream()
                        .filter(patient -> id.equals(patient.getId()))
                        .findFirst().orElse(null);
            } catch (Exception ignored) {}
        }

        if (p != null) {
            billingService.generateMasterOpdInvoice(
                    "OPD-WEB-VISIT",
                    p.getPatientId(),
                    "Dr. Pawan Kumar",
                    BigDecimal.valueOf(consultationFee),
                    BigDecimal.valueOf(labFee),
                    BigDecimal.valueOf(pharmacyFee),
                    BigDecimal.ZERO,
                    paymentMode,
                    "Web Master OPD Invoice"
            );
        }

        return "redirect:/billing";
    }
}
