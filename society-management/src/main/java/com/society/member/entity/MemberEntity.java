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
}