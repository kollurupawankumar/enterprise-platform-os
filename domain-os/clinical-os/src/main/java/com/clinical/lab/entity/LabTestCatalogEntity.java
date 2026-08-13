package com.clinical.lab.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_test_catalog")
public class LabTestCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_code", nullable = false, unique = true)
    private String testCode;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(nullable = false)
    private String category;

    @Column(name = "sample_type")
    private String sampleType;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "normal_range")
    private String normalRange;

    private String units;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public LabTestCatalogEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String normalRange) { this.normalRange = normalRange; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
