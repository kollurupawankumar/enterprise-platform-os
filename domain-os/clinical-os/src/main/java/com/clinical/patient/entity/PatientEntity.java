package com.clinical.patient.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false, unique = true)
    private String patientId;

    @Column(name = "patient_type")
    private String patientType = "NEW";

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String gender;
    private LocalDate dob;

    @Column(name = "profile_photo_path")
    private String profilePhotoPath;

    @Column(nullable = false)
    private String phone;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "address_line2")
    private String addressLine2;

    private String city;
    private String state;
    private String pincode;
    private String country = "India";

    @Column(name = "identity_type")
    private String identityType;

    @Column(name = "identity_number")
    private String identityNumber;

    @Column(name = "marital_status")
    private String maritalStatus;

    private String occupation;
    private String nationality = "Indian";

    @Column(name = "preferred_language")
    private String preferredLanguage = "English";

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_relationship")
    private String emergencyRelationship;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "emergency_alternate_phone")
    private String emergencyAlternatePhone;

    @Column(name = "known_allergies_status")
    private String knownAllergiesStatus = "UNKNOWN";

    private String allergies;

    @Column(name = "patient_reported_alerts")
    private String patientReportedAlerts;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_relationship")
    private String guardianRelationship;

    @Column(name = "guardian_phone")
    private String guardianPhone;

    @Column(name = "guardian_address")
    private String guardianAddress;

    @Column(name = "payment_category")
    private String paymentCategory = "SELF_PAY";

    @Column(name = "insurance_provider")
    private String insuranceProvider;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "preferred_communication")
    private String preferredCommunication = "SMS";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public PatientEntity() {}

    public Integer getCalculatedAge() {
        if (dob == null) return null;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientType() { return patientType; }
    public void setPatientType(String patientType) { this.patientType = patientType; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAlternatePhone() { return alternatePhone; }
    public void setAlternatePhone(String alternatePhone) { this.alternatePhone = alternatePhone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getIdentityType() { return identityType; }
    public void setIdentityType(String identityType) { this.identityType = identityType; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyAlternatePhone() { return emergencyAlternatePhone; }
    public void setEmergencyAlternatePhone(String emergencyAlternatePhone) { this.emergencyAlternatePhone = emergencyAlternatePhone; }

    public String getKnownAllergiesStatus() { return knownAllergiesStatus; }
    public void setKnownAllergiesStatus(String knownAllergiesStatus) { this.knownAllergiesStatus = knownAllergiesStatus; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getPatientReportedAlerts() { return patientReportedAlerts; }
    public void setPatientReportedAlerts(String patientReportedAlerts) { this.patientReportedAlerts = patientReportedAlerts; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getGuardianRelationship() { return guardianRelationship; }
    public void setGuardianRelationship(String guardianRelationship) { this.guardianRelationship = guardianRelationship; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public String getGuardianAddress() { return guardianAddress; }
    public void setGuardianAddress(String guardianAddress) { this.guardianAddress = guardianAddress; }

    public String getPaymentCategory() { return paymentCategory; }
    public void setPaymentCategory(String paymentCategory) { this.paymentCategory = paymentCategory; }

    public String getInsuranceProvider() { return insuranceProvider; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getPreferredCommunication() { return preferredCommunication; }
    public void setPreferredCommunication(String preferredCommunication) { this.preferredCommunication = preferredCommunication; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
