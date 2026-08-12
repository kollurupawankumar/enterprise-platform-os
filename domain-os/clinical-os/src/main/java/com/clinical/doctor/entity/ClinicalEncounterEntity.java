package com.clinical.doctor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_encounter")
public class ClinicalEncounterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "encounter_id", nullable = false, unique = true)
    private String encounterId;

    @Column(name = "visit_id", nullable = false)
    private String visitId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "chief_complaint")
    private String chiefComplaint;

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "pulse_rate")
    private Integer pulseRate;

    private BigDecimal temperature;
    private Integer spo2;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "height_cm")
    private BigDecimal heightCm;

    private BigDecimal bmi;

    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(name = "physical_examination")
    private String physicalExamination;

    @Column(name = "clinical_assessment")
    private String clinicalAssessment;

    @Column(name = "icd_code")
    private String icdCode;

    @Column(name = "icd_description")
    private String icdDescription;

    private String diagnosis;

    @Column(name = "referral_specialty")
    private String referralSpecialty;

    @Column(name = "referral_hospital")
    private String referralHospital;

    @Column(name = "referral_reason")
    private String referralReason;

    private String status = "COMPLETED";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ClinicalEncounterEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEncounterId() { return encounterId; }
    public void setEncounterId(String encounterId) { this.encounterId = encounterId; }

    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public Integer getPulseRate() { return pulseRate; }
    public void setPulseRate(Integer pulseRate) { this.pulseRate = pulseRate; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }

    public BigDecimal getBmi() { return bmi; }
    public void setBmi(BigDecimal bmi) { this.bmi = bmi; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getPhysicalExamination() { return physicalExamination; }
    public void setPhysicalExamination(String physicalExamination) { this.physicalExamination = physicalExamination; }

    public String getClinicalAssessment() { return clinicalAssessment; }
    public void setClinicalAssessment(String clinicalAssessment) { this.clinicalAssessment = clinicalAssessment; }

    public String getIcdCode() { return icdCode; }
    public void setIcdCode(String icdCode) { this.icdCode = icdCode; }

    public String getIcdDescription() { return icdDescription; }
    public void setIcdDescription(String icdDescription) { this.icdDescription = icdDescription; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getReferralSpecialty() { return referralSpecialty; }
    public void setReferralSpecialty(String referralSpecialty) { this.referralSpecialty = referralSpecialty; }

    public String getReferralHospital() { return referralHospital; }
    public void setReferralHospital(String referralHospital) { this.referralHospital = referralHospital; }

    public String getReferralReason() { return referralReason; }
    public void setReferralReason(String referralReason) { this.referralReason = referralReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
