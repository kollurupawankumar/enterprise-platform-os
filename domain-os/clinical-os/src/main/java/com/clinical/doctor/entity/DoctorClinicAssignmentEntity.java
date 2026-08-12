package com.clinical.doctor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctor_clinic_assignment")
public class DoctorClinicAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    private String department;
    private String role = "Consultant";

    @Column(name = "new_patient_fee")
    private BigDecimal newPatientFee = BigDecimal.valueOf(500);

    @Column(name = "followup_fee")
    private BigDecimal followupFee = BigDecimal.valueOf(300);

    @Column(name = "emergency_fee")
    private BigDecimal emergencyFee = BigDecimal.valueOf(800);

    private String status = "ACTIVE";

    public DoctorClinicAssignmentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getNewPatientFee() { return newPatientFee; }
    public void setNewPatientFee(BigDecimal newPatientFee) { this.newPatientFee = newPatientFee; }

    public BigDecimal getFollowupFee() { return followupFee; }
    public void setFollowupFee(BigDecimal followupFee) { this.followupFee = followupFee; }

    public BigDecimal getEmergencyFee() { return emergencyFee; }
    public void setEmergencyFee(BigDecimal emergencyFee) { this.emergencyFee = emergencyFee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
