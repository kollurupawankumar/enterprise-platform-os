package com.clinical.ui.web;

import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebLabController {

    private final LabDiagnosticsService labDiagnosticsService;
    private final PatientService patientService;

    public WebLabController(LabDiagnosticsService labDiagnosticsService, PatientService patientService) {
        this.labDiagnosticsService = labDiagnosticsService;
        this.patientService = patientService;
    }

    @GetMapping({"/lab", "/lab/orders"})
    public String listLabOrders(
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            Model model) {
        model.addAttribute("pageTitle", "Diagnostic Test Orders & Phlebotomy Queue");
        model.addAttribute("activeTab", "lab-orders");

        List<LabOrderEntity> orders = labDiagnosticsService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("totalCount", orders.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(ordered));

        return "lab";
    }

    @GetMapping("/lab/new")
    public String newLabOrderForm(Model model) {
        model.addAttribute("pageTitle", "Create Diagnostic Lab Test Order");
        model.addAttribute("activeTab", "lab-new");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        return "lab_new";
    }

    @PostMapping("/lab/order")
    public String createLabOrder(
            @RequestParam("patientId") String patientId,
            @RequestParam("testCode") String testCode,
            @RequestParam(value = "doctorName", defaultValue = "Dr. Suresh Kumar") String doctorName) {

        String visitId = "VISIT-" + System.currentTimeMillis() % 10000;
        labDiagnosticsService.createLabOrder(visitId, testCode);

        return "redirect:/lab/orders?ordered=true";
    }

    @GetMapping("/lab/result-entry")
    public String resultEntryWorkstation(
            @RequestParam(value = "saved", required = false) Boolean saved,
            Model model) {
        model.addAttribute("pageTitle", "Technician Lab Result Entry Workstation");
        model.addAttribute("activeTab", "lab-result-entry");

        List<LabOrderEntity> orders = labDiagnosticsService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(saved));

        return "lab_result_entry";
    }

    @PostMapping("/lab/result/save")
    public String saveResult(
            @RequestParam("orderId") String orderId,
            @RequestParam("resultValue") String resultValue) {

        labDiagnosticsService.updateLabStatus(orderId, "COMPLETED", resultValue);
        return "redirect:/lab/result-entry?saved=true";
    }

    @GetMapping("/lab/report/{orderId}")
    public String viewLabReport(@PathVariable("orderId") String orderId, Model model) {
        model.addAttribute("pageTitle", "Official Diagnostic Pathology Report");

        LabOrderEntity order = labDiagnosticsService.getAllOrders().stream()
                .filter(o -> orderId.equals(o.getOrderId()))
                .findFirst().orElse(null);

        model.addAttribute("order", order);
        return "lab_report";
    }

    @GetMapping("/lab/catalog")
    public String testCatalog(Model model) {
        model.addAttribute("pageTitle", "Diagnostic Test Master Catalog & Pricing");
        model.addAttribute("activeTab", "lab-catalog");
        model.addAttribute("catalog", labDiagnosticsService.getTestCatalog());
        return "lab_catalog";
    }
}
