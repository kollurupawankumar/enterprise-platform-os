package com.society.operations.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "society_staff")
public class SocietyStaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String role; // SECURITY, HOUSEKEEPING, ESTATE_MANAGER, ELECTRICIAN, PLUMBER

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(name = "id_proof_number", length = 50)
    private String idProofNumber;

    @Column(name = "shift_timing", length = 50)
    private String shiftTiming;

    @Column(name = "police_verification_status", nullable = false, length = 30)
    private String policeVerificationStatus; // VERIFIED, PENDING, REJECTED

    @Column(name = "police_doc_path", length = 255)
    private String policeDocPath;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SocietyStaffEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getIdProofNumber() { return idProofNumber; }
    public void setIdProofNumber(String idProofNumber) { this.idProofNumber = idProofNumber; }

    public String getShiftTiming() { return shiftTiming; }
    public void setShiftTiming(String shiftTiming) { this.shiftTiming = shiftTiming; }

    public String getPoliceVerificationStatus() { return policeVerificationStatus; }
    public void setPoliceVerificationStatus(String policeVerificationStatus) { this.policeVerificationStatus = policeVerificationStatus; }

    public String getPoliceDocPath() { return policeDocPath; }
    public void setPoliceDocPath(String policeDocPath) { this.policeDocPath = policeDocPath; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
