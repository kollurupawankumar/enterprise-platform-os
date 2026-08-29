package com.society.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.Convert;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.society.infrastructure.persistance.converter.BooleanToIntegerConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_number",
            nullable = false,
            unique = true,
            length = 20)
    private String memberNumber;

    @Column(name = "membership_number",
            unique = true,
            length = 30)
    private String membershipNumber;

    @Column(name = "first_name",
            nullable = false,
            length = 100)
    private String firstName;

    @Column(name = "last_name",
            length = 100)
    private String lastName;

    @Column(name = "mobile_number",
            nullable = false,
            length = 15)
    private String mobileNumber;

    @Column(length = 150)
    private String email;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "father_or_spouse_name", length = 150)
    private String fatherOrSpouseName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "dob", length = 20)
    private String dob;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "member_type", length = 50)
    private String memberType;

    @Column(name = "admission_date", length = 20)
    private String admissionDate;

    @Column(name = "resolution_number", length = 50)
    private String resolutionNumber;

    @Column(name = "resolution_date", length = 20)
    private String resolutionDate;

    @Column(name = "permanent_address", length = 500)
    private String permanentAddress;

    @Column(name = "correspondence_address", length = 500)
    private String correspondenceAddress;

    @Column(name = "photo_path", length = 255)
    private String photoPath;

    @Column(name = "aadhaar_doc_path", length = 255)
    private String aadhaarDocPath;

    @Column(name = "pan_doc_path", length = 255)
    private String panDocPath;

    @Column(name = "aadhaar_number",
            length = 20)
    private String aadhaarNumber;

    @Column(name = "pan_number",
            length = 20)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = 20)
    private MemberStatus status;

    @Column(nullable = false)
    @Convert(converter = BooleanToIntegerConverter.class)
    private boolean active;

    @Column(name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MemberEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFatherOrSpouseName() {
        return fatherOrSpouseName;
    }

    public void setFatherOrSpouseName(String fatherOrSpouseName) {
        this.fatherOrSpouseName = fatherOrSpouseName;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }

    public String getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }

    public String getResolutionNumber() { return resolutionNumber; }
    public void setResolutionNumber(String resolutionNumber) { this.resolutionNumber = resolutionNumber; }

    public String getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(String resolutionDate) { this.resolutionDate = resolutionDate; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getCorrespondenceAddress() { return correspondenceAddress; }
    public void setCorrespondenceAddress(String correspondenceAddress) { this.correspondenceAddress = correspondenceAddress; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getAadhaarDocPath() { return aadhaarDocPath; }
    public void setAadhaarDocPath(String aadhaarDocPath) { this.aadhaarDocPath = aadhaarDocPath; }

    public String getPanDocPath() { return panDocPath; }
    public void setPanDocPath(String panDocPath) { this.panDocPath = panDocPath; }

    @Override
    public String toString() {
        String full = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        full = full.trim();
        if (memberNumber != null && !memberNumber.isBlank()) {
            return (full.isEmpty() ? "Member" : full) + " (" + memberNumber + ")";
        }
        return full.isEmpty() ? "Member #" + id : full;
    }
}