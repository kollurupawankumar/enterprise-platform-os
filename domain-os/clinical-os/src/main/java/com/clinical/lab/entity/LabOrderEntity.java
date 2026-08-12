package com.clinical.lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_order")
public class LabOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "visit_id", nullable = false)
    private String visitId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "encounter_id")
    private String encounterId;

    @Column(name = "test_name", nullable = false)
    private String testName;

    private String status = "ORDERED"; // ORDERED, SAMPLE_COLLECTED, IN_PROCESS, COMPLETED, REVIEWED

    private String result;

    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public LabOrderEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getEncounterId() { return encounterId; }
    public void setEncounterId(String encounterId) { this.encounterId = encounterId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
