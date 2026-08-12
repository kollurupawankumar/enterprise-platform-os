package com.clinical.doctor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false, unique = true)
    private String doctorId;

    private String title = "Dr.";

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "display_name")
    private String displayName;

    @Column(nullable = false)
    private String name;

    private String gender;
    private LocalDate dob;

    @Column(name = "profile_photo_path")
    private String profilePhotoPath;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "registration_authority")
    private String registrationAuthority;

    @Column(name = "registration_state")
    private String registrationState;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    private String qualification;

    private String specialization;

    private String phone;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    private String email;

    private String address;
    private String city;
    private String state;
    private String pincode;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee = BigDecimal.valueOf(500);

    @Column(name = "lifecycle_status")
    private String lifecycleStatus = "ACTIVE"; // PENDING, ACTIVE, INACTIVE, SUSPENDED, TERMINATED

    private Integer active = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public DoctorEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getRegistrationAuthority() { return registrationAuthority; }
    public void setRegistrationAuthority(String registrationAuthority) { this.registrationAuthority = registrationAuthority; }

    public String getRegistrationState() { return registrationState; }
    public void setRegistrationState(String registrationState) { this.registrationState = registrationState; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAlternatePhone() { return alternatePhone; }
    public void setAlternatePhone(String alternatePhone) { this.alternatePhone = alternatePhone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }

    public Integer getActive() { return active; }
    public void setActive(Integer active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
