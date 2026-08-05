package com.society.operations.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset")
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String category; // ELEVATOR, GENERATOR

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "purchase_date", length = 20)
    private String purchaseDate;

    @Column(name = "purchase_cost")
    private Double purchaseCost;

    @Column(name = "warranty_expiry_date", length = 20)
    private String warrantyExpiryDate;

    @Column(nullable = false, length = 30)
    private String status; // OPERATIONAL, UNDER_MAINTENANCE, SCRAPPED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amc_vendor_id")
    private VendorEntity amcVendor;

    @Column(name = "amc_start_date", length = 20)
    private String amcStartDate;

    @Column(name = "amc_expiry_date", length = 20)
    private String amcExpiryDate;

    @Column(name = "amc_cost")
    private Double amcCost;

    @Column(name = "amc_details", length = 500)
    private String amcDetails;

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

    public AssetEntity() {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(Double purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public String getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(String warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
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

    public VendorEntity getAmcVendor() { return amcVendor; }
    public void setAmcVendor(VendorEntity amcVendor) { this.amcVendor = amcVendor; }

    public String getAmcStartDate() { return amcStartDate; }
    public void setAmcStartDate(String amcStartDate) { this.amcStartDate = amcStartDate; }

    public String getAmcExpiryDate() { return amcExpiryDate; }
    public void setAmcExpiryDate(String amcExpiryDate) { this.amcExpiryDate = amcExpiryDate; }

    public Double getAmcCost() { return amcCost; }
    public void setAmcCost(Double amcCost) { this.amcCost = amcCost; }

    public String getAmcDetails() { return amcDetails; }
    public void setAmcDetails(String amcDetails) { this.amcDetails = amcDetails; }
}
