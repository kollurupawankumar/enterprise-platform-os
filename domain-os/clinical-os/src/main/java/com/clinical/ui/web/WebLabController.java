package com.clinical.ui.web;

import com.clinical.lab.service.LabDiagnosticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebLabController {

    private final LabDiagnosticsService labDiagnosticsService;

    public WebLabController(LabDiagnosticsService labDiagnosticsService) {
        this.labDiagnosticsService = labDiagnosticsService;
    }

    @GetMapping("/lab")
    public String listLab(Model model) {
        model.addAttribute("pageTitle", "Diagnostic Test Catalog & Lab Orders");
        model.addAttribute("activeTab", "lab");
        model.addAttribute("catalog", labDiagnosticsService.getTestCatalog());
        model.addAttribute("orders", labDiagnosticsService.getAllOrders());
        return "lab";
    }
}
