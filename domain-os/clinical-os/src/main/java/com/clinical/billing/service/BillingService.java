package com.clinical.billing.service;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BillingService {
    InvoiceEntity generateInvoice(String visitId, String patientId, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy);
    InvoiceEntity generateMasterOpdInvoice(String visitId, String patientId, String doctorName, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy, BigDecimal discount, String paymentMode, String remarks);
    InvoiceEntity processPayment(String invoiceId, String paymentMode);
    Optional<InvoiceEntity> getInvoiceByVisit(String visitId);
    List<InvoiceEntity> getAllInvoices();
    Map<String, Object> getDailyCashRegisterSummary(LocalDate date);
}

@Service
@Transactional
class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public BillingServiceImpl(InvoiceRepository invoiceRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.invoiceRepository = invoiceRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public InvoiceEntity generateInvoice(String visitId, String patientId, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy) {
        return generateMasterOpdInvoice(visitId, patientId, "Dr. Prescriber", consultation, lab, pharmacy, BigDecimal.ZERO, "CASH", "Standard OPD Invoice");
    }

    @Override
    public InvoiceEntity generateMasterOpdInvoice(String visitId, String patientId, String doctorName, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy, BigDecimal discount, String paymentMode, String remarks) {
        InvoiceEntity invoice = new InvoiceEntity();
        long count = invoiceRepository.count() + 1;
        invoice.setInvoiceId(idGenerator.generateId("INVOICE_ID_FORMAT", "INV-{YYYY}-{SEQ6}", count));
        invoice.setVisitId(visitId != null ? visitId : "OPD-WALKIN");
        invoice.setPatientId(patientId != null ? patientId : "PAT-WALKIN");
        invoice.setDoctorName(doctorName != null ? doctorName : "Opd Doctor");
        
        BigDecimal cFee = consultation != null ? consultation : BigDecimal.ZERO;
        BigDecimal lFee = lab != null ? lab : BigDecimal.ZERO;
        BigDecimal pFee = pharmacy != null ? pharmacy : BigDecimal.ZERO;
        
        invoice.setConsultationFee(cFee);
        invoice.setLabFee(lFee);
        invoice.setPharmacyFee(pFee);

        BigDecimal subtotal = cFee.add(lFee).add(pFee);
        BigDecimal disc = discount != null ? discount : BigDecimal.ZERO;
        BigDecimal afterDisc = subtotal.subtract(disc);
        if (afterDisc.compareTo(BigDecimal.ZERO) < 0) afterDisc = BigDecimal.ZERO;
        BigDecimal tax = afterDisc.multiply(new BigDecimal("0.05")); // 5% GST
        BigDecimal total = afterDisc.add(tax);

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(disc);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setPaymentMode(paymentMode != null ? paymentMode : "CASH");
        invoice.setPaymentStatus("PAID");
        invoice.setRemarks(remarks != null ? remarks : "Settled at Central OPD Counter");

        return invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceEntity processPayment(String invoiceId, String paymentMode) {
        InvoiceEntity invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));
        invoice.setPaymentMode(paymentMode);
        invoice.setPaymentStatus("PAID");
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceEntity> getInvoiceByVisit(String visitId) {
        return invoiceRepository.findByVisitId(visitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceEntity> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDailyCashRegisterSummary(LocalDate date) {
        List<InvoiceEntity> invoices = invoiceRepository.findAll();
        LocalDate targetDate = date != null ? date : LocalDate.now();

        BigDecimal cashTotal = BigDecimal.ZERO;
        BigDecimal upiTotal = BigDecimal.ZERO;
        BigDecimal cardTotal = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;
        BigDecimal netRevenue = BigDecimal.ZERO;
        int count = 0;

        for (InvoiceEntity inv : invoices) {
            if (inv.getCreatedAt() != null && inv.getCreatedAt().toLocalDate().equals(targetDate)) {
                count++;
                BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
                BigDecimal disc = inv.getDiscountAmount() != null ? inv.getDiscountAmount() : BigDecimal.ZERO;
                discountTotal = discountTotal.add(disc);
                netRevenue = netRevenue.add(amt);

                String mode = inv.getPaymentMode() != null ? inv.getPaymentMode().toUpperCase() : "CASH";
                if (mode.contains("CASH")) {
                    cashTotal = cashTotal.add(amt);
                } else if (mode.contains("UPI")) {
                    upiTotal = upiTotal.add(amt);
                } else if (mode.contains("CARD")) {
                    cardTotal = cardTotal.add(amt);
                } else {
                    cashTotal = cashTotal.add(amt);
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("date", targetDate.toString());
        summary.put("totalInvoices", count);
        summary.put("cashTotal", cashTotal);
        summary.put("upiTotal", upiTotal);
        summary.put("cardTotal", cardTotal);
        summary.put("discountTotal", discountTotal);
        summary.put("netRevenue", netRevenue);
        return summary;
    }
}

