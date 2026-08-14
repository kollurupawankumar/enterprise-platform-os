package com.clinical.pharmacy.repository;

import com.clinical.pharmacy.entity.PharmacyInvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyInvoiceItemRepository extends JpaRepository<PharmacyInvoiceItemEntity, Long> {
    List<PharmacyInvoiceItemEntity> findByInvoiceId(String invoiceId);
}
