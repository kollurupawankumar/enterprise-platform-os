package com.clinical.billing.repository;

import com.clinical.billing.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    Optional<InvoiceEntity> findByInvoiceId(String invoiceId);
    Optional<InvoiceEntity> findByVisitId(String visitId);
}
