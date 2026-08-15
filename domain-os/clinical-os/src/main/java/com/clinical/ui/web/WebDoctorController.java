package com.clinical.ui.web;

import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.service.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebDoctorController {

    private final DoctorService doctorService;

    public WebDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("pageTitle", "Doctor Roster & Onboarding");
        model.addAttribute("activeTab", "doctors");

        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "doctors";
    }

    @PostMapping("/doctors/register")
    public String registerDoctor(
            @RequestParam("name") String name,
            @RequestParam("specialization") String specialization,
            @RequestParam(value = "qualification", required = false) String qualification,
            @RequestParam(value = "licenseNumber", required = false) String licenseNumber,
            @RequestParam(value = "phone", required = false) String phone) {

        DoctorEntity doc = new DoctorEntity();
        doc.setName(name.trim());
        doc.setSpecialization(specialization.trim());
        doc.setQualification(qualification != null ? qualification.trim() : "MBBS");
        doc.setLicenseNumber(licenseNumber != null ? licenseNumber.trim() : "MCI-" + System.currentTimeMillis() % 10000);
        doc.setPhone(phone != null ? phone.trim() : "+91 98765 43210");

        doctorService.registerDoctor(doc);
        return "redirect:/doctors";
    }
}
