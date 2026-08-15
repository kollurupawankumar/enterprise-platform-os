package com.clinical.ui.web;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.repository.ClinicalEncounterRepository;
import com.clinical.doctor.service.DoctorConsultationService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebConsultationController {

    private final DoctorConsultationService consultationService;
    private final ClinicalEncounterRepository encounterRepository;
    private final PatientService patientService;

    public WebConsultationController(
            DoctorConsultationService consultationService,
            ClinicalEncounterRepository encounterRepository,
            PatientService patientService) {
        this.consultationService = consultationService;
        this.encounterRepository = encounterRepository;
        this.patientService = patientService;
    }

    @GetMapping("/consultation")
    public String listConsultations(Model model) {
        model.addAttribute("pageTitle", "Doctor EMR & Rx Workstation");
        model.addAttribute("activeTab", "consultation");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        List<ClinicalEncounterEntity> encounters = encounterRepository.findAll();
        model.addAttribute("encounters", encounters);

        return "consultation";
    }

    @PostMapping("/consultation/record")
    public String recordConsultation(
            @RequestParam("patientId") String patientId,
            @RequestParam("chiefComplaint") String chiefComplaint,
            @RequestParam(value = "bp", required = false) String bp,
            @RequestParam(value = "pulseRate", required = false) Integer pulseRate,
            @RequestParam(value = "icdCode", required = false) String icdCode,
            @RequestParam(value = "clinicalAssessment", required = false) String clinicalAssessment) {

        ClinicalEncounterEntity enc = new ClinicalEncounterEntity();
        enc.setVisitId("VISIT-" + System.currentTimeMillis() % 10000);
        enc.setPatientId(patientId);
        enc.setDoctorId("DOC-101");
        enc.setChiefComplaint(chiefComplaint.trim());

        if (bp != null && bp.contains("/")) {
            try {
                String[] parts = bp.split("/");
                enc.setSystolicBp(Integer.parseInt(parts[0].trim()));
                enc.setDiastolicBp(Integer.parseInt(parts[1].trim()));
            } catch (Exception ignored) {}
        }
        enc.setPulseRate(pulseRate != null ? pulseRate : 72);
        enc.setIcdCode(icdCode != null ? icdCode.trim() : "J06.9");
        enc.setClinicalAssessment(clinicalAssessment != null ? clinicalAssessment.trim() : "Prescribed Medication & Rest");

        consultationService.recordEncounter(enc);
        return "redirect:/consultation";
    }
}
