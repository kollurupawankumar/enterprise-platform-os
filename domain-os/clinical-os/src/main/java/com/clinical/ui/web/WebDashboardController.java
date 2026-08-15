package com.clinical.ui.web;

import com.clinical.appointment.service.AppointmentService;
import com.clinical.billing.service.BillingService;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class WebDashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;

    public WebDashboardController(
            PatientService patientService,
            AppointmentService appointmentService,
            BillingService billingService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Executive Dashboard");
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("totalPatients", patientService.getAllPatients().size());
        
        var todayAppointments = appointmentService.getAppointmentsByDateAndDoctor(LocalDate.now(), "");
        model.addAttribute("todayAppointments", todayAppointments.size());
        model.addAttribute("recentAppointments", todayAppointments);

        double totalRevenue = billingService.getAllInvoices().stream()
                .mapToDouble(inv -> inv.getTotalAmount() != null ? inv.getTotalAmount().doubleValue() : 0.0)
                .sum();

        model.addAttribute("formattedRevenue", String.format("₹%,.2f", totalRevenue));
        model.addAttribute("pharmacyOrders", 8);

        return "dashboard";
    }
}
