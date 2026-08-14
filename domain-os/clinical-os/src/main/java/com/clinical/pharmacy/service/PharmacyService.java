package com.clinical.pharmacy.service;

import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceItemEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.pharmacy.repository.PharmacyInvoiceItemRepository;
import com.clinical.pharmacy.repository.PharmacyInvoiceRepository;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PharmacyService {
    MedicineInventoryEntity addOrUpdateStock(MedicineInventoryEntity medicine);
    boolean dispensePrescription(PrescriptionEntity prescription);
    List<MedicineInventoryEntity> searchMedicine(String medicineName);
    List<MedicineInventoryEntity> getAllStock();
    PharmacyInvoiceEntity createPharmacyInvoice(String patientId, String visitId, String doctorName, List<PharmacyInvoiceItemEntity> items, BigDecimal discount, String paymentStatus, String paymentMode);
    List<PharmacyInvoiceEntity> getAllInvoices();
    List<PharmacyInvoiceItemEntity> getInvoiceItems(String invoiceId);
}

@Service
@Transactional
class PharmacyServiceImpl implements PharmacyService {

    private final MedicineInventoryRepository inventoryRepository;
    private final PharmacyInvoiceRepository invoiceRepository;
    private final PharmacyInvoiceItemRepository invoiceItemRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public PharmacyServiceImpl(MedicineInventoryRepository inventoryRepository,
                                PharmacyInvoiceRepository invoiceRepository,
                                PharmacyInvoiceItemRepository invoiceItemRepository,
                                com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.inventoryRepository = inventoryRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public MedicineInventoryEntity addOrUpdateStock(MedicineInventoryEntity medicine) {
        return inventoryRepository.save(medicine);
    }

    @Override
    public boolean dispensePrescription(PrescriptionEntity prescription) {
        for (PrescriptionItemEntity item : prescription.getItems()) {
            List<MedicineInventoryEntity> stock = inventoryRepository.findByMedicineName(item.getMedicineName());
            if (!stock.isEmpty()) {
                MedicineInventoryEntity batch = stock.get(0);
                if (batch.getQuantity() > 0) {
                    batch.setQuantity(batch.getQuantity() - 1);
                    inventoryRepository.save(batch);
                }
            }
        }
        prescription.setStatus("DISPENSED");
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventoryEntity> searchMedicine(String medicineName) {
        return inventoryRepository.findByMedicineName(medicineName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventoryEntity> getAllStock() {
        return inventoryRepository.findAll();
    }

    @Override
    public PharmacyInvoiceEntity createPharmacyInvoice(String patientId, String visitId, String doctorName, List<PharmacyInvoiceItemEntity> items, BigDecimal discount, String paymentStatus, String paymentMode) {
        PharmacyInvoiceEntity invoice = new PharmacyInvoiceEntity();
        long count = invoiceRepository.count() + 1;
        String invoiceId = idGenerator.generateId("PHARMACY_INVOICE_FORMAT", "PHARM-{YYYY}-{SEQ6}", count);
        invoice.setInvoiceId(invoiceId);
        invoice.setPatientId(patientId);
        invoice.setVisitId(visitId != null && !visitId.isBlank() ? visitId : "WALK-IN");
        invoice.setDoctorName(doctorName != null && !doctorName.isBlank() ? doctorName : "Self / OTC");
        invoice.setPaymentStatus(paymentStatus != null ? paymentStatus : "PAID");
        invoice.setPaymentMode(paymentMode != null ? paymentMode : "CASH");
        invoice.setStatus("DISPENSED");

        BigDecimal subtotal = BigDecimal.ZERO;
        List<PharmacyInvoiceItemEntity> savedItems = new ArrayList<>();

        for (PharmacyInvoiceItemEntity item : items) {
            item.setInvoiceId(invoiceId);
            BigDecimal line = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setLineTotal(line);
            subtotal = subtotal.add(line);

            // Deduct inventory stock
            if (item.getBatchNumber() != null) {
                Optional<MedicineInventoryEntity> optStock = inventoryRepository.findByMedicineNameAndBatchNumber(item.getMedicineName(), item.getBatchNumber());
                if (optStock.isPresent()) {
                    MedicineInventoryEntity stock = optStock.get();
                    int rem = stock.getQuantity() - item.getQuantity();
                    stock.setQuantity(Math.max(0, rem));
                    inventoryRepository.save(stock);
                }
            }
        }

        BigDecimal disc = discount != null ? discount : BigDecimal.ZERO;
        BigDecimal tax = subtotal.subtract(disc).multiply(new BigDecimal("0.05")); // 5% GST
        BigDecimal total = subtotal.subtract(disc).add(tax);

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(disc);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        PharmacyInvoiceEntity savedInvoice = invoiceRepository.save(invoice);
        List<PharmacyInvoiceItemEntity> persistedItems = invoiceItemRepository.saveAll(items);
        savedInvoice.setItems(persistedItems);
        return savedInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyInvoiceEntity> getAllInvoices() {
        List<PharmacyInvoiceEntity> invoices = invoiceRepository.findAll();
        for (PharmacyInvoiceEntity inv : invoices) {
            inv.setItems(invoiceItemRepository.findByInvoiceId(inv.getInvoiceId()));
        }
        return invoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyInvoiceItemEntity> getInvoiceItems(String invoiceId) {
        return invoiceItemRepository.findByInvoiceId(invoiceId);
    }
}

