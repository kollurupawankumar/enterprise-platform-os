package com.society.society.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "society")
public class SocietyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "short_name", nullable = false, length = 100)
    private String shortName;

    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_year_start_month", nullable = false, length = 20)
    private FinancialYearStartMonth financialYearStartMonth;

    @Column(name = "member_number_format", length = 100)
    private String memberNumberFormat;

    @Column(name = "share_certificate_format", length = 100)
    private String shareCertificateFormat;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(name = "seal_path", length = 500)
    private String sealPath;

    @Column(name = "active", nullable = false)
    private Boolean active;

    public SocietyEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public FinancialYearStartMonth getFinancialYearStartMonth() {
        return financialYearStartMonth;
    }

    public void setFinancialYearStartMonth(FinancialYearStartMonth financialYearStartMonth) {
        this.financialYearStartMonth = financialYearStartMonth;
    }

    public String getMemberNumberFormat() {
        return memberNumberFormat;
    }

    public void setMemberNumberFormat(String memberNumberFormat) {
        this.memberNumberFormat = memberNumberFormat;
    }

    public String getShareCertificateFormat() {
        return shareCertificateFormat;
    }

    public void setShareCertificateFormat(String shareCertificateFormat) {
        this.shareCertificateFormat = shareCertificateFormat;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getSealPath() {
        return sealPath;
    }

    public void setSealPath(String sealPath) {
        this.sealPath = sealPath;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}