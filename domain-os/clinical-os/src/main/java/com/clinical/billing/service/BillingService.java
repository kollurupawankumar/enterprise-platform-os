package com.clinical.billing.service;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

public interface BillingService {
    InvoiceEntity generateInvoice(String visitId, String patientId, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy);
    InvoiceEntity processPayment(String invoiceId, String paymentMode);
    Optional<InvoiceEntity> getInvoiceByVisit(String visitId);
}

@Service
@Transactional
class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;

    public BillingServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public InvoiceEntity generateInvoice(String visitId, String patientId, BigDecimal consultation, BigDecimal lab, BigDecimal pharmacy) {
        InvoiceEntity invoice = new InvoiceEntity();
        long count = invoiceRepository.count() + 1;
        invoice.setInvoiceId(String.format("INV-%06d", count));
        invoice.setVisitId(visitId);
        invoice.setPatientId(patientId);
        invoice.setConsultationFee(consultation != null ? consultation : BigDecimal.ZERO);
        invoice.setLabFee(lab != null ? lab : BigDecimal.ZERO);
        invoice.setPharmacyFee(pharmacy != null ? pharmacy : BigDecimal.ZERO);
        invoice.setTotalAmount(invoice.getConsultationFee().add(invoice.getLabFee()).add(invoice.getPharmacyFee()));
        invoice.setPaymentStatus("UNPAID");
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
}
