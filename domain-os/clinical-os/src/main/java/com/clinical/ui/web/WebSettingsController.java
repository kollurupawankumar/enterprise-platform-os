package com.clinical.ui.web;

import com.clinical.admin.service.ClinicSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebSettingsController {

    private final ClinicSettingService clinicSettingService;

    public WebSettingsController(ClinicSettingService clinicSettingService) {
        this.clinicSettingService = clinicSettingService;
    }

    @GetMapping("/settings")
    public String showSettings(Model model) {
        model.addAttribute("pageTitle", "Clinic Settings & Configuration");
        model.addAttribute("activeTab", "settings");

        model.addAttribute("clinicName", clinicSettingService.getSetting("clinic_name", "City Super Specialty Clinic"));
        model.addAttribute("registrationNumber", clinicSettingService.getSetting("clinic_reg_no", "REG-CLINIC-2026-9001"));
        model.addAttribute("contactPhone", clinicSettingService.getSetting("clinic_phone", "+91 98765 43210"));
        model.addAttribute("contactEmail", clinicSettingService.getSetting("clinic_email", "contact@cityclinic.com"));
        model.addAttribute("address", clinicSettingService.getSetting("clinic_address", "Suite 404, Health City Complex, MG Road, Tech City"));

        return "settings";
    }

    @PostMapping("/settings/save")
    public String saveSettings(
            @RequestParam("clinicName") String clinicName,
            @RequestParam(value = "registrationNumber", required = false) String registrationNumber,
            @RequestParam(value = "contactPhone", required = false) String contactPhone,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam(value = "address", required = false) String address) {

        clinicSettingService.saveSetting("clinic_name", clinicName);
        if (registrationNumber != null) clinicSettingService.saveSetting("clinic_reg_no", registrationNumber);
        if (contactPhone != null) clinicSettingService.saveSetting("clinic_phone", contactPhone);
        if (contactEmail != null) clinicSettingService.saveSetting("clinic_email", contactEmail);
        if (address != null) clinicSettingService.saveSetting("clinic_address", address);

        return "redirect:/settings";
    }
}
