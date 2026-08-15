package com.clinical.ui.web;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebPatientController {

    private final PatientService patientService;

    public WebPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public String listPatients(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "registered", required = false) Boolean registered,
            Model model) {
        
        model.addAttribute("pageTitle", "Master Patient Directory");
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
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(registered));

        return "patients";
    }

    @GetMapping("/patients/register")
    public String registerPatientForm(Model model) {
        model.addAttribute("pageTitle", "New Patient Onboarding");
        model.addAttribute("activeTab", "patient-register");
        return "patient_register";
    }

    @PostMapping("/patients/register")
    public String registerPatient(
            @RequestParam("firstName") String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "alternatePhone", required = false) String alternatePhone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) String dobStr,
            @RequestParam(value = "patientType", required = false) String patientType,
            @RequestParam(value = "identityType", required = false) String identityType,
            @RequestParam(value = "identityNumber", required = false) String identityNumber,
            @RequestParam(value = "maritalStatus", required = false) String maritalStatus,
            @RequestParam(value = "occupation", required = false) String occupation,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "addressLine2", required = false) String addressLine2,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "pincode", required = false) String pincode,
            @RequestParam(value = "emergencyContactName", required = false) String emergencyContactName,
            @RequestParam(value = "emergencyRelationship", required = false) String emergencyRelationship,
            @RequestParam(value = "emergencyContactPhone", required = false) String emergencyContactPhone,
            @RequestParam(value = "knownAllergiesStatus", required = false) String knownAllergiesStatus,
            @RequestParam(value = "allergies", required = false) String allergies) {

        PatientEntity p = new PatientEntity();
        p.setFirstName(firstName.trim());
        p.setLastName(lastName != null ? lastName.trim() : "");
        p.setPhone(phone.trim());
        p.setAlternatePhone(alternatePhone != null ? alternatePhone.trim() : null);
        p.setEmail(email != null ? email.trim() : null);
        p.setGender(gender != null ? gender : "MALE");
        
        if (dobStr != null && !dobStr.isBlank()) {
            try {
                p.setDob(LocalDate.parse(dobStr.trim()));
            } catch (Exception ignored) {}
        }
        
        p.setPatientType(patientType != null ? patientType : "NEW");
        p.setIdentityType(identityType != null ? identityType : "AADHAAR");
        p.setIdentityNumber(identityNumber != null ? identityNumber.trim() : null);
        p.setMaritalStatus(maritalStatus != null ? maritalStatus : "SINGLE");
        p.setOccupation(occupation != null ? occupation.trim() : null);

        p.setAddress(address != null ? address.trim() : null);
        p.setAddressLine2(addressLine2 != null ? addressLine2.trim() : null);
        p.setCity(city != null ? city.trim() : null);
        p.setState(state != null ? state.trim() : null);
        p.setPincode(pincode != null ? pincode.trim() : null);

        p.setEmergencyContactName(emergencyContactName != null ? emergencyContactName.trim() : null);
        p.setEmergencyRelationship(emergencyRelationship != null ? emergencyRelationship.trim() : null);
        p.setEmergencyContactPhone(emergencyContactPhone != null ? emergencyContactPhone.trim() : null);
        p.setKnownAllergiesStatus(knownAllergiesStatus != null ? knownAllergiesStatus : "UNKNOWN");
        p.setAllergies(allergies != null ? allergies.trim() : null);

        patientService.registerPatient(p);
        return "redirect:/patients?registered=true";
    }
}
