package com.society.finance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment")
public class InvestmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "investment_type", nullable = false, length = 30)
    private String investmentType; // FD, MUTUAL_FUND, GOVT_BOND

    @Column(nullable = false, length = 150)
    private String institution;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Column(name = "principal_amount", nullable = false)
    private Double principalAmount;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "start_date", nullable = false, length = 20)
    private String startDate;

    @Column(name = "maturity_date", length = 20)
    private String maturityDate;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, MATURED, LIQUIDATED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    public InvestmentEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
