package com.clinical.doctor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral")
public class ReferralEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referral_id", nullable = false, unique = true)
    private String referralId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "encounter_id", nullable = false)
    private String encounterId;

    @Column(name = "referring_doctor_id", nullable = false)
    private String referringDoctorId;

    @Column(name = "target_specialty")
    private String targetSpecialty;

    @Column(name = "target_hospital")
    private String targetHospital;

    @Column(name = "target_doctor_name")
    private String targetDoctorName;

    private String reason;
    private String urgency = "ROUTINE";
    private String status = "PENDING";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ReferralEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReferralId() { return referralId; }
    public void setReferralId(String referralId) { this.referralId = referralId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getEncounterId() { return encounterId; }
    public void setEncounterId(String encounterId) { this.encounterId = encounterId; }

    public String getReferringDoctorId() { return referringDoctorId; }
    public void setReferringDoctorId(String referringDoctorId) { this.referringDoctorId = referringDoctorId; }

    public String getTargetSpecialty() { return targetSpecialty; }
    public void setTargetSpecialty(String targetSpecialty) { this.targetSpecialty = targetSpecialty; }

    public String getTargetHospital() { return targetHospital; }
    public void setTargetHospital(String targetHospital) { this.targetHospital = targetHospital; }

    public String getTargetDoctorName() { return targetDoctorName; }
    public void setTargetDoctorName(String targetDoctorName) { this.targetDoctorName = targetDoctorName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
