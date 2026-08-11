package com.clinical.doctor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_encounter")
public class ClinicalEncounterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_id", nullable = false, unique = true)
    private String visitId;

    @Column(name = "chief_complaints")
    private String chiefComplaints;

    private String temperature;
    private String bp;
    private String pulse;
    private String spo2;
    private String weight;
    private String height;

    @Column(name = "clinical_notes")
    private String clinicalNotes;

    private String diagnosis;

    @Column(name = "icd_code")
    private String icdCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ClinicalEncounterEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }

    public String getChiefComplaints() { return chiefComplaints; }
    public void setChiefComplaints(String chiefComplaints) { this.chiefComplaints = chiefComplaints; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getBp() { return bp; }
    public void setBp(String bp) { this.bp = bp; }

    public String getPulse() { return pulse; }
    public void setPulse(String pulse) { this.pulse = pulse; }

    public String getSpo2() { return spo2; }
    public void setSpo2(String spo2) { this.spo2 = spo2; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getIcdCode() { return icdCode; }
    public void setIcdCode(String icdCode) { this.icdCode = icdCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
