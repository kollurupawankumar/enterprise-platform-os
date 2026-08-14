package com.clinical.pharmacy.repository;

import com.clinical.pharmacy.entity.PharmacyInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyInvoiceRepository extends JpaRepository<PharmacyInvoiceEntity, Long> {
    Optional<PharmacyInvoiceEntity> findByInvoiceId(String invoiceId);
    List<PharmacyInvoiceEntity> findByPatientId(String patientId);
    List<PharmacyInvoiceEntity> findByVisitId(String visitId);
}
