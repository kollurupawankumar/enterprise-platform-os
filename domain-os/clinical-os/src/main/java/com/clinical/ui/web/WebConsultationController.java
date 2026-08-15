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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public String listConsultations(
            @RequestParam(value = "recorded", required = false) Boolean recorded,
            Model model) {
        model.addAttribute("pageTitle", "Doctor EMR Encounters & Consultations");
        model.addAttribute("activeTab", "consultation");

        List<ClinicalEncounterEntity> encounters = encounterRepository.findAll();
        model.addAttribute("encounters", encounters);
        model.addAttribute("totalCount", encounters.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(recorded));

        return "consultation";
    }

    @GetMapping("/consultation/new")
    public String newConsultationForm(Model model) {
        model.addAttribute("pageTitle", "Clinical EMR Consultation Workstation");
        model.addAttribute("activeTab", "consultation-new");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        return "consultation_new";
    }

    @PostMapping("/consultation/record")
    public String recordConsultation(
            @RequestParam("patientId") String patientId,
            @RequestParam("chiefComplaint") String chiefComplaint,
            @RequestParam(value = "systolicBp", required = false) Integer systolicBp,
            @RequestParam(value = "diastolicBp", required = false) Integer diastolicBp,
            @RequestParam(value = "pulseRate", required = false) Integer pulseRate,
            @RequestParam(value = "temperature", required = false) Double tempVal,
            @RequestParam(value = "spo2", required = false) Integer spo2,
            @RequestParam(value = "weightKg", required = false) Double weightVal,
            @RequestParam(value = "heightCm", required = false) Double heightVal,
            @RequestParam(value = "icdCode", required = false) String icdCode,
            @RequestParam(value = "icdDescription", required = false) String icdDescription,
            @RequestParam(value = "clinicalAssessment", required = false) String clinicalAssessment,
            @RequestParam(value = "treatmentPlan", required = false) String treatmentPlan) {

        ClinicalEncounterEntity enc = new ClinicalEncounterEntity();
        enc.setVisitId("VISIT-" + System.currentTimeMillis() % 10000);
        enc.setPatientId(patientId);
        enc.setDoctorId("DOC-101");
        enc.setChiefComplaint(chiefComplaint.trim());

        enc.setSystolicBp(systolicBp != null ? systolicBp : 120);
        enc.setDiastolicBp(diastolicBp != null ? diastolicBp : 80);
        enc.setPulseRate(pulseRate != null ? pulseRate : 72);
        enc.setSpo2(spo2 != null ? spo2 : 98);

        if (tempVal != null) {
            enc.setTemperature(BigDecimal.valueOf(tempVal));
        } else {
            enc.setTemperature(BigDecimal.valueOf(98.6));
        }

        if (weightVal != null) enc.setWeightKg(BigDecimal.valueOf(weightVal));
        if (heightVal != null) enc.setHeightCm(BigDecimal.valueOf(heightVal));

        // Calculate BMI if weight and height are provided
        if (weightVal != null && heightVal != null && heightVal > 0) {
            double heightM = heightVal / 100.0;
            double bmiVal = weightVal / (heightM * heightM);
            enc.setBmi(BigDecimal.valueOf(bmiVal).setScale(1, RoundingMode.HALF_UP));
        }

        enc.setIcdCode((icdCode != null && !icdCode.isBlank()) ? icdCode.trim() : "J06.9");
        enc.setIcdDescription((icdDescription != null && !icdDescription.isBlank()) ? icdDescription.trim() : "Acute Upper Respiratory Infection");
        enc.setClinicalAssessment((clinicalAssessment != null && !clinicalAssessment.isBlank()) ? clinicalAssessment.trim() : "Prescribed symptomatic medication & bed rest.");
        enc.setDiagnosis((treatmentPlan != null && !treatmentPlan.isBlank()) ? treatmentPlan.trim() : "Follow up in 5 days if fever persists.");

        consultationService.recordEncounter(enc);
        return "redirect:/consultation?recorded=true";
    }
}
