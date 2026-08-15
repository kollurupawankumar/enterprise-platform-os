package com.clinical.ui.web;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebPatientController {

    private final PatientService patientService;

    public WebPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public String listPatients(@RequestParam(value = "query", required = false) String query, Model model) {
        model.addAttribute("pageTitle", "Patient Directory");
        model.addAttribute("activeTab", "patients");

        List<PatientEntity> allPatients = patientService.getAllPatients();
        List<PatientEntity> patients;

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            patients = allPatients.stream()
                    .filter(p -> (p.getPatientId() != null && p.getPatientId().toLowerCase().contains(q)) ||
                                 (p.getFirstName() != null && p.getFirstName().toLowerCase().contains(q)) ||
                                 (p.getLastName() != null && p.getLastName().toLowerCase().contains(q)) ||
                                 (p.getPhone() != null && p.getPhone().contains(q)))
                    .collect(Collectors.toList());
        } else {
            patients = allPatients;
        }

        model.addAttribute("patients", patients);
        model.addAttribute("query", query != null ? query : "");

        return "patients";
    }

    @PostMapping("/patients/register")
    public String registerPatient(
            @RequestParam("firstName") String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "gender", required = false) String gender) {

        PatientEntity p = new PatientEntity();
        p.setFirstName(firstName.trim());
        p.setLastName(lastName != null ? lastName.trim() : "");
        p.setPhone(phoneNumber.trim());
        p.setGender(gender != null ? gender : "MALE");

        patientService.registerPatient(p);
        return "redirect:/patients";
    }
}
