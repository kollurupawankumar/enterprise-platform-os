package com.clinical.ui.web;

import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/lab")
    public String listLab(
            @RequestParam(value = "ordered", required = false) Boolean ordered,
            Model model) {
        model.addAttribute("pageTitle", "Lab Diagnostics & Test Orders");
        model.addAttribute("activeTab", "lab");

        model.addAttribute("catalog", labDiagnosticsService.getTestCatalog());
        model.addAttribute("orders", labDiagnosticsService.getAllOrders());
        model.addAttribute("totalCount", labDiagnosticsService.getAllOrders().size());
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

        return "redirect:/lab?ordered=true";
    }
}
