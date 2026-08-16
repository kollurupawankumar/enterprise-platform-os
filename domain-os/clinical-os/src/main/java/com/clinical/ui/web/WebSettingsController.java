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

    @GetMapping({"/settings", "/settings/clinic"})
    public String showClinicSettings(
            @RequestParam(value = "saved", required = false) Boolean saved,
            Model model) {
        model.addAttribute("pageTitle", "Hospital Info & Branding Setup");
        model.addAttribute("activeTab", "settings-clinic");

        model.addAttribute("clinicName", clinicSettingService.getSetting("clinic_name", "Clinical OS Healthcare Network"));
        model.addAttribute("registrationNumber", clinicSettingService.getSetting("clinic_reg_no", "CLINIC-REG-2026-HYD"));
        model.addAttribute("contactPhone", clinicSettingService.getSetting("clinic_phone", "+91 40 6789 0000"));
        model.addAttribute("contactEmail", clinicSettingService.getSetting("clinic_email", "contact@clinicalos.com"));
        model.addAttribute("address", clinicSettingService.getSetting("clinic_address", "Road No. 12, Jubilee Hills, Hyderabad - 500034"));
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(saved));

        return "settings_clinic";
    }

    @PostMapping("/settings/clinic")
    public String saveClinicSettings(
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

        return "redirect:/settings/clinic?saved=true";
    }

    @GetMapping("/settings/users")
    public String showUserSettings(Model model) {
        model.addAttribute("pageTitle", "User Accounts & Role Credentials");
        model.addAttribute("activeTab", "settings-users");
        return "settings_users";
    }

    @GetMapping("/settings/patterns")
    public String showPatternSettings(
            @RequestParam(value = "saved", required = false) Boolean saved,
            Model model) {
        model.addAttribute("pageTitle", "Auto Generated ID Sequence Patterns");
        model.addAttribute("activeTab", "settings-patterns");

        model.addAttribute("patientIdPattern", clinicSettingService.getSetting("PATIENT_ID_FORMAT", "PAT-{YYYY}-{SEQ}"));
        model.addAttribute("tokenPattern", clinicSettingService.getSetting("TOKEN_ID_FORMAT", "TOK-{SEQ}"));
        model.addAttribute("doctorIdPattern", clinicSettingService.getSetting("DOCTOR_ID_FORMAT", "DOC-{SEQ}"));
        model.addAttribute("encounterIdPattern", clinicSettingService.getSetting("ENCOUNTER_ID_FORMAT", "ENC-{YYYY}-{SEQ}"));
        model.addAttribute("invoiceIdPattern", clinicSettingService.getSetting("INVOICE_ID_FORMAT", "INV-{YYYY}-{SEQ}"));
        model.addAttribute("pharmacyIdPattern", clinicSettingService.getSetting("PHARMACY_ID_FORMAT", "RX-{YYYY}-{SEQ}"));
        model.addAttribute("labIdPattern", clinicSettingService.getSetting("LAB_ID_FORMAT", "LAB-{YYYY}-{SEQ}"));
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(saved));

        return "settings_patterns";
    }

    @PostMapping("/settings/patterns")
    public String savePatternSettings(
            @RequestParam(value = "patientIdPattern", required = false) String patientIdPattern,
            @RequestParam(value = "tokenPattern", required = false) String tokenPattern,
            @RequestParam(value = "doctorIdPattern", required = false) String doctorIdPattern,
            @RequestParam(value = "encounterIdPattern", required = false) String encounterIdPattern,
            @RequestParam(value = "invoiceIdPattern", required = false) String invoiceIdPattern,
            @RequestParam(value = "pharmacyIdPattern", required = false) String pharmacyIdPattern,
            @RequestParam(value = "labIdPattern", required = false) String labIdPattern) {

        if (patientIdPattern != null) clinicSettingService.saveSetting("PATIENT_ID_FORMAT", patientIdPattern);
        if (tokenPattern != null) clinicSettingService.saveSetting("TOKEN_ID_FORMAT", tokenPattern);
        if (doctorIdPattern != null) clinicSettingService.saveSetting("DOCTOR_ID_FORMAT", doctorIdPattern);
        if (encounterIdPattern != null) clinicSettingService.saveSetting("ENCOUNTER_ID_FORMAT", encounterIdPattern);
        if (invoiceIdPattern != null) clinicSettingService.saveSetting("INVOICE_ID_FORMAT", invoiceIdPattern);
        if (pharmacyIdPattern != null) clinicSettingService.saveSetting("PHARMACY_ID_FORMAT", pharmacyIdPattern);
        if (labIdPattern != null) clinicSettingService.saveSetting("LAB_ID_FORMAT", labIdPattern);

        return "redirect:/settings/patterns?saved=true";
    }
}
