package com.clinical.pharmacy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicine_inventory")
public class MedicineInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    private Integer quantity = 0;
    
    @Column(name = "reorder_level")
    private Integer reorderLevel = 20;

    private String supplier;
    private String location;

    public MedicineInventoryEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
