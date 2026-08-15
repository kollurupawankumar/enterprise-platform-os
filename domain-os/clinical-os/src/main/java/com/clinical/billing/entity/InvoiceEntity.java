package com.clinical.billing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false, unique = true)
    private String invoiceId;

    @Column(name = "visit_id", nullable = false)
    private String visitId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @Column(name = "lab_fee")
    private BigDecimal labFee = BigDecimal.ZERO;

    @Column(name = "pharmacy_fee")
    private BigDecimal pharmacyFee = BigDecimal.ZERO;

    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "doctor_name")
    private String doctorName;

    private String remarks;

    @Column(name = "payment_status")
    private String paymentStatus = "UNPAID"; // UNPAID, PAID, PARTIAL

    @Column(name = "payment_mode")
    private String paymentMode; // CASH, UPI, CARD, BANK_TRANSFER

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public InvoiceEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public BigDecimal getLabFee() { return labFee; }
    public void setLabFee(BigDecimal labFee) { this.labFee = labFee; }

    public BigDecimal getPharmacyFee() { return pharmacyFee; }
    public void setPharmacyFee(BigDecimal pharmacyFee) { this.pharmacyFee = pharmacyFee; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

