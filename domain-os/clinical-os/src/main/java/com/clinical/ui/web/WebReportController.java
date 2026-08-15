package com.clinical.ui.web;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebReportController {

    private final BillingService billingService;

    public WebReportController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        model.addAttribute("pageTitle", "Practice Analytics & Financial Reports");
        model.addAttribute("activeTab", "reports");

        List<InvoiceEntity> invoices = billingService.getAllInvoices();

        double totalConsultation = invoices.stream()
                .mapToDouble(i -> i.getConsultationFee() != null ? i.getConsultationFee().doubleValue() : 0.0)
                .sum();

        double totalLab = invoices.stream()
                .mapToDouble(i -> i.getLabFee() != null ? i.getLabFee().doubleValue() : 0.0)
                .sum();

        double totalPharmacy = invoices.stream()
                .mapToDouble(i -> i.getPharmacyFee() != null ? i.getPharmacyFee().doubleValue() : 0.0)
                .sum();

        double totalRevenue = invoices.stream()
                .mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount().doubleValue() : 0.0)
                .sum();

        model.addAttribute("formattedTotalRevenue", String.format("₹%,.2f", totalRevenue));
        model.addAttribute("formattedConsultationRevenue", String.format("₹%,.2f", totalConsultation));
        model.addAttribute("formattedPharmacyRevenue", String.format("₹%,.2f", totalPharmacy));
        model.addAttribute("formattedLabRevenue", String.format("₹%,.2f", totalLab));

        return "reports";
    }
}
